import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.TargetDataLine;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class Taligenkanning {

    private static final int STREAMING_LIMIT = 290000; // ~5 minuter på sig. Google tillåter bara 5-min sessioner. Startar om efter 5 min (spelet avbryts inte utan fortsätter)

    private static final String RED = "\033[0;31m";
    private static final String GREEN = "\033[0;32m";
    private static final String YELLOW = "\033[0;33m";

    // Creating shared object
    private static volatile BlockingQueue<byte[]> sharedQueue = new LinkedBlockingQueue();
    private static TargetDataLine targetDataLine;
    private static int BYTES_PER_BUFFER = 6400; // buffer size in bytes
    private static ArrayList<ByteString> audioInput = new ArrayList<>();
    private static ArrayList<ByteString> lastAudioInput = new ArrayList<>();
    private static String detSomSadesTidigare = "";
    private static int resultEndTimeInMS = 0;
    private static int isFinalEndTime = 0;
    private static int finalRequestEndTime = 0;
    private static int restartCounter = 0;
    private static boolean newStream = true;
    static boolean nyssFelsvar = false;
    static boolean nyssKorrekt = false;
    static boolean inspelningKlar = false;
    private static double bridgingOffset = 0;
    private static boolean lastTranscriptWasFinal = false;
    private static StreamController referenceToStreamController;
    private static ByteString tempByteString;


    /**
     * Kontinuerlig taligenkänning
     */
    static void starta(String languageCode) throws Exception {

        // Microphone Input buffering
        class MicBuffer implements Runnable {

            @Override
            public void run() {
                System.out.println(YELLOW);
                System.out.println("Taligenkänning redo.");
                Frame.startaLabel.setText("\u25B6 'Starta spelet'");
                targetDataLine.start();
                byte[] data = new byte[BYTES_PER_BUFFER];
                while (targetDataLine.isOpen()) {
                    try {
                        int numBytesRead = targetDataLine.read(data, 0, data.length);
                        if ((numBytesRead <= 0) && (targetDataLine.isOpen())) {
                            continue;
                        }
                        sharedQueue.put(data.clone());
                    } catch (InterruptedException e) {
                        System.out.println("Microphone input buffering interrupted : " + e.getMessage());
                    }
                }
            }
        }

        // Creating microphone input buffer thread
        MicBuffer micrunnable = new MicBuffer();
        Thread micThread = new Thread(micrunnable);
        ResponseObserver<StreamingRecognizeResponse> responseObserver = null;
        try (SpeechClient client = SpeechClient.create()) {
            ClientStream<StreamingRecognizeRequest> clientStream;
            responseObserver =
                    new ResponseObserver<StreamingRecognizeResponse>() {

                        ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

                        public void onStart(StreamController controller) {
                            referenceToStreamController = controller;
                        }

                        public void onResponse(StreamingRecognizeResponse response) {
                            responses.add(response);
                            if (response.getResultsList().size() != 0) {


                                StreamingRecognitionResult result = response.getResultsList().get(0);
                                Duration resultEndTime = result.getResultEndTime();
                                resultEndTimeInMS = (int) ((resultEndTime.getSeconds() * 1000)
                                        + (resultEndTime.getNanos() / 1000000));

                                String res = result.getAlternativesList().get(0).getTranscript();

                                //Byter ut ej tydligt definerade ord (som t.ex. skillnaden mellan silver och grå)
                                //samt ord som ofta missuppfattas av taligenkänningen eller som har flera namn.
                                if (res.contains("silver")) {
                                    res = res.replaceAll("silver", "grå");
                                }
                                if (res.contains("Lichtenstein")) {
                                    res = res.replaceAll("Lichtenstein", "Liechtenstein");
                                }
                                if (res.contains("guld")) {
                                    res = res.replaceAll("guld", "gul");
                                }
                                if (res.contains("Bosnien")) {
                                    res = res.replaceAll("Bosnien", "Bosnien och Hercegovina");
                                }
                                if (res.contains("Makedonien") || res.contains("nordmakedonien")) {
                                    res = res.replaceAll("Makedonien", "Nordmakedonien");
                                    res = res.replaceAll("nordmakedonien", "Nordmakedonien");
                                }
                                if (res.contains("chisinau")) {
                                    res = res.replaceAll("chisinau", "Chisinau");
                                }
                                if (res.contains("beige")) {
                                    res = res.replaceAll("beige", "brun");
                                }
                                if (res.contains("Andorra La vella")) {
                                    res = res.replaceAll("Andorra La vella", "Andorra la Vella");
                                }
                                if (res.contains("vaduz")) {
                                    res = res.replaceAll("vaduz", "Vaduz");
                                }
                                if (res.contains("västeuropa")) {
                                    res = res.replaceAll("västeuropa", "Västeuropa");
                                }
                                if (res.contains("Vatikanstaten") && res.contains("stad")) {
                                    res = res.replaceAll("Vatikanstaten", "Vatikanstaden");
                                }
                                if (res.contains("Louisiana") || res.contains("Juliana")) {
                                    res += "Ljubljana";
                                }
                                if (res.contains("stad") && res.contains(" vin")) {
                                    res = res.replaceAll(" vin", " Wien");
                                }


                                if (nyssKorrekt) {
                                    if (!nyssFelsvar) {
                                        // "res" = det tal som taligenkänningen (GCS) har uppfattat.
                                        // "d" = det som sagts EFTER att användaren fått rätt.
                                        if (res.length() <= detSomSadesTidigare.length()) {
                                        } else if (res.length() > 2 && detSomSadesTidigare.length() > 2) {
                                            String d = res.substring(detSomSadesTidigare.length() - 1);
                                            Logik.check(d);
                                        } else {
                                            Logik.check(res);
                                        }
                                    }
                                } else {
                                    if (res.length() > 0) {
                                        detSomSadesTidigare = res;
                                    }
                                    if (!nyssFelsvar) {
                                        Logik.check(res);
                                    }
                                }

                                // "result.getIsFinal()" är en boolean från GCS som har värdet
                                // "true" då GCS har uppfattat en mening som avslutad.
                                if (result.getIsFinal()) {
                                    nyssFelsvar = false;
                                    nyssKorrekt = false;
                                    detSomSadesTidigare = "";

                                    System.out.print(GREEN);
//                                    System.out.print("\033[2K\r");
                                    System.out.println(res);
                                    isFinalEndTime = resultEndTimeInMS;

                                    System.out.println("result.getIsFinal() == true;");
                                    lastTranscriptWasFinal = true;
                                    detSomSadesTidigare = "";

                                } else {
                                    System.out.print(RED);
//                                    System.out.print("\033[2K\r");
                                    System.out.println(res);

                                    lastTranscriptWasFinal = false;
                                }

                            }
                        }

                        public void onComplete() {
                            System.out.println("COMPLETE. [onComplete()]");
                        }

                        public void onError(Throwable t) {
                            System.out.println("ERROR. [onError()] t: " + t.getMessage());
                            t.printStackTrace();
                            t.getCause().printStackTrace();
                        }

                    };

            clientStream = client.streamingRecognizeCallable().splitCall(responseObserver);

            RecognitionConfig recognitionConfig =
                    RecognitionConfig.newBuilder()
                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                            .setLanguageCode(languageCode)
                            .setSampleRateHertz(16000)
                            .build();

            StreamingRecognitionConfig streamingRecognitionConfig =
                    StreamingRecognitionConfig.newBuilder()
                            .setConfig(recognitionConfig)
                            .setInterimResults(true)
                            .build();

            StreamingRecognizeRequest request =
                    StreamingRecognizeRequest.newBuilder()
                            .setStreamingConfig(streamingRecognitionConfig)
                            .build(); // The first request in a streaming call has to be a config

            clientStream.send(request);

            try {
                // SampleRate:16000Hz, SampleSizeInBits: 16, Number of channels: 1, Signed: true,
                // bigEndian: false
                AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
                Info targetInfo =
                        new Info(
                                TargetDataLine.class,
                                audioFormat); // Set the system information to read from the microphone audio
                // stream

                if (!AudioSystem.isLineSupported(targetInfo)) {
                    System.out.println("Ingen mikrofon upptäckt.");
                    System.exit(0);
                }
                // Target data line captures the audio stream the microphone produces.
                targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
                targetDataLine.open(audioFormat);
                micThread.start();

                long startTime = System.currentTimeMillis();
                while (true) {
                    long estimatedTime = System.currentTimeMillis() - startTime;
                    if (estimatedTime >= STREAMING_LIMIT) {

                        clientStream.closeSend();
                        referenceToStreamController.cancel(); // remove Observer

                        if (resultEndTimeInMS > 0) {
                            finalRequestEndTime = isFinalEndTime;
                        }
                        resultEndTimeInMS = 0;

                        lastAudioInput = null;
                        lastAudioInput = audioInput;
                        audioInput = new ArrayList<ByteString>();

                        restartCounter++;

                        if (!lastTranscriptWasFinal) {
                            System.out.print('\n');
                        }

                        newStream = true;

                        clientStream = client.streamingRecognizeCallable().splitCall(responseObserver);

                        request =
                                StreamingRecognizeRequest.newBuilder()
                                        .setStreamingConfig(streamingRecognitionConfig)
                                        .build();

                        System.out.println(YELLOW);
                        System.out.printf("%d: RESTARTING REQUEST\n", restartCounter * STREAMING_LIMIT);

                        startTime = System.currentTimeMillis();

                    } else {

                        if ((newStream) && (lastAudioInput.size() > 0)) {
                            // if this is the first audio from a new request
                            // calculate amount of unfinalized audio from last request
                            // resend the audio to the speech client before incoming audio
                            double chunkTime = STREAMING_LIMIT / lastAudioInput.size();
                            // ms length of each chunk in previous request audio arrayList
                            if (chunkTime != 0) {
                                if (bridgingOffset < 0) {
                                    // bridging Offset accounts for time of resent audio
                                    // calculated from last request
                                    bridgingOffset = 0;
                                }
                                if (bridgingOffset > finalRequestEndTime) {
                                    bridgingOffset = finalRequestEndTime;
                                }
                                int chunksFromMS = (int) Math.floor((finalRequestEndTime
                                        - bridgingOffset) / chunkTime);
                                // chunks from MS is number of chunks to resend
                                bridgingOffset = (int) Math.floor((lastAudioInput.size()
                                        - chunksFromMS) * chunkTime);
                                // set bridging offset for next request
                                for (int i = chunksFromMS; i < lastAudioInput.size(); i++) {
                                    request =
                                            StreamingRecognizeRequest.newBuilder()
                                                    .setAudioContent(lastAudioInput.get(i))
                                                    .build();
                                    clientStream.send(request);
                                }
                            }
                            newStream = false;
                        }

                        tempByteString = ByteString.copyFrom(sharedQueue.take());

                        request =
                                StreamingRecognizeRequest.newBuilder()
                                        .setAudioContent(tempByteString)
                                        .build();

                        audioInput.add(tempByteString);

                    }

                    clientStream.send(request);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }


}
