package org.Journal;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicJournal implements Journal {

    private final AtomicInteger systemChangeNumber;
    private final Map<Integer, Integer> uncommittedTransactions;
    private final Map<Integer, Boolean> isCommited;
    private final Map<Integer, List<RedoLogRecord>> redoLogs;
    private final Map<Integer, List<UndoLogRecord>> undoLogs;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final static String WAL_FILE_PATH = "./src/main/java/org/Journal/wal.log";

    public BasicJournal(Integer checkpointIntervalInSecond) throws IOException {
        Path walPath = Paths.get(WAL_FILE_PATH);
        if(!Files.exists(walPath)) {
            try{
                Files.createFile(walPath);
            } catch (IOException ioException) {
                System.out.println("An error occurred while creating WAL file -> " + ioException.getMessage());
            }
        }

        systemChangeNumber = new AtomicInteger(0);
        uncommittedTransactions = new HashMap<>();
        isCommited = new HashMap<>();
        redoLogs = new HashMap<>();
        undoLogs = new HashMap<>();
        scheduler.scheduleAtFixedRate(() -> {
            try{
                this.checkpoint();
            } catch (IOException ioException){
                System.out.println(ioException.getMessage());
            }
        }, 5 , checkpointIntervalInSecond, TimeUnit.SECONDS);
    }

    @Override
    public Integer beginTransaction() throws IOException {

        try(FileChannel fileChannel = FileChannel.open(Paths.get(WAL_FILE_PATH), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
            BeginLog beginLog = new BeginLog(systemChangeNumber.incrementAndGet());
            ByteBuffer byteBuffer = ByteBuffer.wrap(beginLog.toString().getBytes());
            fileChannel.write(byteBuffer);
            uncommittedTransactions.put(beginLog.systemChangeNumber(), beginLog.systemChangeNumber());
            isCommited.put(beginLog.systemChangeNumber(), false);
            return beginLog.systemChangeNumber();
        }

    }

    @Override
    public void commit(Integer systemChangeNumber) throws IOException{
        List<RedoLogRecord> redoLogRecordList = this.redoLogs.get(systemChangeNumber);
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

        redoLogRecordList.forEach(redoLog -> {
            byteBuffer.put(new WriteLogRecord(redoLog.scn(), redoLog.command(), redoLog.key(), redoLog.newValue(), redoLog.oldValue()).toString().getBytes());
        });
        byteBuffer.put(new CommitLog(systemChangeNumber).toString().getBytes());
        byteBuffer.flip();
        try(FileChannel fileChannel = FileChannel.open(Paths.get(WAL_FILE_PATH), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {

            fileChannel.write(byteBuffer);
            fileChannel.force(true);
            isCommited.put(systemChangeNumber, true);
            uncommittedTransactions.remove(systemChangeNumber);
            redoLogs.remove(systemChangeNumber);
            undoLogs.remove(systemChangeNumber);
        }

    }

    @Override
    public void rollback(Integer systemChangeNumber) throws IOException {
        var undoLogs = this.undoLogs.get(systemChangeNumber);
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        try(FileChannel fileChannel = FileChannel.open(Paths.get(WAL_FILE_PATH), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
            undoLogs.forEach(undoLog -> {
                byteBuffer.put(new WriteLogRecord(undoLog.scn(), undoLog.command(), undoLog.key(), undoLog.oldValue(), undoLog.newValue()).toString().getBytes());
            });
            byteBuffer.put(new AbortLog(systemChangeNumber).toString().getBytes());
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            fileChannel.force(true);
            this.undoLogs.remove(systemChangeNumber);
            this.isCommited.put(systemChangeNumber, true);
            uncommittedTransactions.remove(systemChangeNumber);
        }

    }

    @Override
    public void write(Integer systemChangeNumber, LogCommand command, String key, String newValue, String oldValue) throws IOException {
            redoLogs.computeIfAbsent(systemChangeNumber, k -> new ArrayList<>())
                    .add(new RedoLogRecord(systemChangeNumber, command, key, newValue, oldValue));
            undoLogs
                    .computeIfAbsent(systemChangeNumber, k -> new ArrayList<>())
                    .add(new UndoLogRecord(systemChangeNumber, command, key, oldValue, newValue));



    }

    @Override
    public void recovery() throws IOException {

        try (FileChannel channel = FileChannel.open(Paths.get(WAL_FILE_PATH), StandardOpenOption.READ, StandardOpenOption.WRITE)) {

            long fileSize = channel.size();

            ByteBuffer buffer = ByteBuffer.allocate(1); // Tek tek karakter okumak için

            StringBuilder line = new StringBuilder();
            Stack<String> stack = new Stack<>();
            for (long pos = fileSize - 1; pos >= 0; pos--) {
                channel.position(pos);
                buffer.clear();
                channel.read(buffer);
                buffer.flip();

                char c = (char) buffer.get();
                if (c == '\n') {
                    if (!line.isEmpty()) {
                        String isEndCheckpointOrStartCheckpoint = line.reverse().toString();
                        if(isEndCheckpointOrStartCheckpoint.startsWith("END CKPT")){
                            stack.push(isEndCheckpointOrStartCheckpoint);
                            System.out.println(stack.peek()+" found");
                        }
                        if(!stack.isEmpty() && isEndCheckpointOrStartCheckpoint.startsWith("START CKPT") && Objects.equals(stack.peek(), "END CKPT")){
                            channel.position(pos);
                            System.out.println(isEndCheckpointOrStartCheckpoint + " found");
                            line.setLength(0);
                            break;
                        }
                        line.setLength(0);
                    }
                } else {
                    line.append(c);
                }
            }
            try(BufferedReader reader = new BufferedReader(Channels.newReader(channel, "UTF-8"))){
                String logLine;

                while ((logLine = reader.readLine()) != null) {
                    //get uncommitted transaction
                    if(logLine.startsWith("START CKPT")) {
                        var scnNumbers = logLine.substring(11);
                       for(String scn : scnNumbers.split(",")){
                            isCommited.put(Integer.valueOf(scn), false);
                       }
                    }
                    if(logLine.startsWith("WRITE")) {
                        var writeLine = logLine.substring(6);
                        var writeLineList = writeLine.split(",");
                        write(Integer.valueOf(writeLineList[0]), LogCommand.valueOf(writeLineList[1]), writeLineList[2], writeLineList[3], writeLineList[4]);
                    }
                    //if transaction committed or aborted, remove from isCommited map.
                    if(logLine.startsWith("COMMIT") || logLine.startsWith("ABORT")){
                        var commitedOrAbortedTransactionScn = Character.getNumericValue(logLine.charAt(logLine.length() - 1));
                        isCommited.remove(commitedOrAbortedTransactionScn);
                        undoLogs.remove(commitedOrAbortedTransactionScn);
                        redoLogs.remove(commitedOrAbortedTransactionScn);
                    }

                }
                //if transaction uncommitted, abort transaction and flush to disk
                if(!isCommited.isEmpty()) {
                    isCommited.forEach((k, v) -> {
                        try {
                            rollback(k);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        }
    }

    private void checkpoint() throws IOException {
        System.out.println("Checkpoint stage started");
        List<Integer> unCommitedTransactionsList = uncommittedTransactions.values()
                    .stream()
                    .toList();

        if(unCommitedTransactionsList.isEmpty()) {
            return;
        }

        StringBuilder systemChangeNumbers = new StringBuilder();
        for(Integer unCommitedTransactionNumber : unCommitedTransactionsList){
            systemChangeNumbers.append(unCommitedTransactionNumber).append(",");
        }
        //Deleting last unnecessary comma
        systemChangeNumbers.deleteCharAt(systemChangeNumbers.length() - 1);

        ByteBuffer redoLogsBuffer = ByteBuffer.allocate(4096);

        for(Integer scn : unCommitedTransactionsList) {
            if(isCommited.get(scn)) {
                continue;
            }
            List<RedoLogRecord> redoLogRecordList = this.redoLogs.get(scn);
            if(redoLogRecordList == null) {
                continue;
            }
            redoLogRecordList.forEach(redoLog -> {
                redoLogsBuffer.put(new WriteLogRecord(redoLog.scn(), redoLog.command(), redoLog.key(), redoLog.newValue(), redoLog.oldValue()).toString().getBytes());
            });
        }

        redoLogsBuffer.flip();
        try(FileChannel fileChannel = FileChannel.open(Paths.get(WAL_FILE_PATH), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {

            var checkpointStartBuffer = ByteBuffer.wrap(new CheckpointLog("START CKPT ", systemChangeNumbers.toString()).toString().getBytes());
            fileChannel.write(checkpointStartBuffer);

            fileChannel.write(redoLogsBuffer);

            var checkpointEndBuffer = ByteBuffer.wrap(new CheckpointLog("END CKPT", "").toString().getBytes());
            fileChannel.write(checkpointEndBuffer);

            fileChannel.force(true);
        }

        System.out.println("Checkpoint stage ended !");
    }
}
