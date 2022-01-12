package com.konradrej.rcpc.server.network.handler;

import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Base handler implementation which allows for easy sending
 * and receiving of messages using the input and output queue.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 2.0
 * @since 2.0
 */
public abstract class SocketHandler implements Runnable {
    private final Logger LOGGER;

    protected final Socket socket;
    protected final BlockingQueue<JSONObject> inputQueue = new LinkedBlockingQueue<>();
    protected final BlockingQueue<JSONObject> outputQueue = new LinkedBlockingQueue<>();

    protected Reader reader = null;
    protected Writer writer = null;
    private Thread readerThread;
    private Thread writerThread;
    protected boolean disconnect = false;

    /**
     * Simplified constructor which enables both input and output and passes existing object streams.
     *
     * @param socket         the connected socket
     * @param LOGGER         logger to be used, can be null to disable
     * @param bufferedWriter object output stream to pass, null to create from socket
     * @param bufferedReader object input stream to pass, null to create from socket
     * @since 2.0
     */
    public SocketHandler(Socket socket, Logger LOGGER, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        this(socket, true, true, LOGGER, bufferedWriter, bufferedReader);
    }

    /**
     * Constructor to customize input and output streams being disabled or not as well as adds possibility to pass existing streams.
     *
     * @param socket         the connected socket
     * @param inputEnabled   whether to enable input
     * @param outputEnabled  whether to enable output
     * @param LOGGER         logger to be used, can be null to disable
     * @param bufferedWriter object output stream to pass, null to create from socket
     * @param bufferedReader object input stream to pass, null to create from socket
     * @since 2.0
     */
    public SocketHandler(Socket socket, boolean inputEnabled, boolean outputEnabled, Logger LOGGER, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        this.socket = socket;
        this.LOGGER = LOGGER;

        if (outputEnabled) {
            if (bufferedWriter != null) {
                writer = new Writer(bufferedWriter);
            } else {
                try {
                    bufferedWriter = new BufferedWriter((new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8)));
                    writer = new Writer(bufferedWriter);
                } catch (IOException e) {
                    if (LOGGER != null) {
                        LOGGER.error("Could not construct writer: " + e.getLocalizedMessage());
                    }
                }
            }

            writerThread = new Thread(writer);
            writerThread.start();
        }

        if (inputEnabled) {
            if (bufferedReader != null) {
                reader = new Reader(bufferedReader);
            } else {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8));
                    reader = new Reader(bufferedReader);
                } catch (IOException e) {
                    if (LOGGER != null) {
                        LOGGER.error("Could not construct reader: " + e.getLocalizedMessage());
                    }
                }
            }

            readerThread = new Thread(reader);
            readerThread.start();
        }
    }

    @Override
    public abstract void run();

    /**
     * Disconnects the current socket.
     *
     * @since 2.0
     */
    public void disconnect() {
        disconnect = true;

        if (reader != null) {
            while (readerThread.isAlive()) {
            }
        }

        if (writer != null) {
            while (writerThread.isAlive()) {
            }
        }

        try {
            socket.close();
        } catch (IOException ignored) {
        }

        if (LOGGER != null) {
            LOGGER.info("Socket disconnected.");
        }
    }

    private class Reader implements Runnable {
        private final BufferedReader bufferedReader;

        public Reader(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public void run() {
            try {
                while (!disconnect) {
                    JSONObject message = new JSONObject(bufferedReader.readLine());

                    inputQueue.add(message);
                }
            } catch (IOException e) {
                if (LOGGER != null) {
                    if (!disconnect) {
                        LOGGER.error("Error while getting message: " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    private class Writer implements Runnable {
        private final BufferedWriter bufferedWriter;

        public Writer(BufferedWriter bufferedWriter) {
            this.bufferedWriter = bufferedWriter;
        }

        @Override
        public void run() {
            try {
                while (!disconnect) {
                    if (outputQueue.size() > 0) {
                        JSONObject message = outputQueue.take();

                        bufferedWriter.write(message.toString());
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                }

                bufferedWriter.flush();
            } catch (IOException | InterruptedException e) {
                if (LOGGER != null) {
                    if (!disconnect) {
                        LOGGER.error("Error while sending message: " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }
}
