package client;

import domain.Appointment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class Client {

    BufferedReader stdIn;

    public static void main(String[] args) throws IOException {
        BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));

        System.out.println("enter server host name");
        String hostName = stdIn.readLine();

        System.out.println("enter server port");
        int portNumber = Integer.parseInt(stdIn.readLine());

        while (true) {
            System.out.println("enter command");
            String command = stdIn.readLine();
            String result, commandString = command + "|";

            if (Objects.equals(command, "exit")) break;
            else if (Objects.equals(command, "programare")) {
                System.out.println("nume: ");
                String nume = stdIn.readLine();
                System.out.println("cnp: ");
                String cnp = stdIn.readLine();
                System.out.println("data: ");
                String data = stdIn.readLine();
                System.out.println("locatie: ");
                String locatie = stdIn.readLine();
                System.out.println("tip tratament: ");
                String tipTratament = stdIn.readLine();
                System.out.println("ora: ");
                String ora = stdIn.readLine();
                commandString += nume + "|" + cnp + "|" + data + "|" + locatie + "|" + tipTratament + "|" + ora;
            } else if (Objects.equals(command, "plata")) {
                System.out.println("data: ");
                String data = stdIn.readLine();
                System.out.println("cnp: ");
                String cnp = stdIn.readLine();
                System.out.println("suma: ");
                String suma = stdIn.readLine();
                commandString += data + "|" + cnp + "|" + suma;
            }

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
