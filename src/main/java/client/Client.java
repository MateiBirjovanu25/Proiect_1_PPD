package client;

import domain.Appointment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;

public class Client {

    BufferedReader stdIn;

    public static void main(String[] args) throws IOException {
        BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));
        Random rand = new Random();
        String hostName = "localhost";
        int portNumber = 1234;

        while (true) {
            String result, commandString;

            String nume = "a";
            String cnp = "b";
            String data = "c";

            int locatie = rand.nextInt(5);
            locatie++;

            int tipTratament = rand.nextInt(5);
            tipTratament++;

            int oraStart = rand.nextInt(8);
            oraStart += 10;
            int oraFinish = oraStart + rand.nextInt(8) + 1;
            if (oraFinish > 18) oraFinish = 18;

            commandString = "programare|";
            commandString += nume + "|" + cnp + "|" + data + "|" + locatie + "|" + tipTratament + "|" + oraStart + "|" + oraFinish;

            try (
                    Socket socket = new Socket(hostName, portNumber);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            ) {
                out.println(commandString);
                result = in.readLine();
            }
            System.out.println(result);

            data = "c";
            cnp = "b";
            int programare = Integer.parseInt(result);

            commandString = "plata|";
            commandString += data + "|" + cnp + "|" + programare;

            try (
                    Socket socket = new Socket(hostName, portNumber);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            ) {
                out.println(commandString);
                result = in.readLine();
            }
            System.out.println(result);

            if (rand.nextDouble() > 0.5) {
                commandString = "anulare|";

                try (
                        Socket socket = new Socket(hostName, portNumber);
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                ) {
                    out.println(commandString);
                    result = in.readLine();
                }
                System.out.println(result);
            }
        }
    }
}
