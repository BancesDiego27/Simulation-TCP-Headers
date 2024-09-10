import java.util.*;

// Clase para representar paquetes de red
class NetworkPacket {
    int index;
    String status;
    String emisor;
    int srcPort;
    int dstPort;
    String flags;
    int seqNumber;
    int ackNumber;
    int len;
    int window;
    String datos;

    public NetworkPacket(int index, String status, String emisor, int srcPort, int dstPort, String flags, int seqNumber, int ackNumber, int len, int window, String datos) {
        this.index = index;
        this.status = status;
        this.emisor = emisor;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.flags = flags;
        this.seqNumber = seqNumber;
        this.ackNumber = ackNumber;
        this.len = len;
        this.window = window;
        this.datos = datos;
    }

    public void printPacket() {
        System.out.printf("%-5d %-8s %-10s %-8d %-8d %-19s %-12d %-12d %-4d %-6d %-6s%n",
                index, status, emisor, srcPort, dstPort, flags, seqNumber, ackNumber, len, window, datos);
    }
}

// Simulación de transmisión sin sockets
public class TransmissionSimulation {

    private static int packetIndex = 1;
    //Separa el String ( cualquier texto ) y ahora lo separa dependiendo de un valor random y lo guarda en un List
    public static List<String> splitText(String text, int minSize, int maxSize) {
        List<String> parts = new ArrayList<>();
        Random random = new Random();
        int index = 0;

        while (index < text.length()) {
            int splitSize = random.nextInt((maxSize - minSize) + 1) + minSize;
            int endIndex = Math.min(index + splitSize, text.length());

            int spaceIndex = text.lastIndexOf(" ", endIndex);
            if (spaceIndex != -1 && spaceIndex > index) {
                parts.add(text.substring(index, spaceIndex).trim());
                index = spaceIndex + 1;
            } else {
                parts.add(text.substring(index, endIndex).trim());
                index = endIndex;
            }
        }
        return parts;
    }
    // Recibe las dos listas y calcula la moda de los paquetes 
    public static int calculateMode(List<String> list1, List<String> list2) {
        Map<Integer, Integer> lengthFrequency = new HashMap<>();

        for (String s : list1) {
            lengthFrequency.put(s.length(), lengthFrequency.getOrDefault(s.length(), 0) + 1);
        }

        for (String s : list2) {
            lengthFrequency.put(s.length(), lengthFrequency.getOrDefault(s.length(), 0) + 1);
        }

        int mode = -1;
        int maxCount = 0;

        for (Map.Entry<Integer, Integer> entry : lengthFrequency.entrySet()) {
            if (entry.getValue() > maxCount) {
                mode = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        return mode;
    }
    // Simula el envio de paquetes pudiendo perderse
    public static boolean sendPacket(String data, double lossProbability) {
        Random random = new Random();
        if (random.nextDouble() < lossProbability) {
            System.out.println("Paquete perdido: " + data);
            return false;
        } else {
            return true;
        }
    }



    //Crea e impirme paquetes conforme el objeto creado de NetworkPacket
    public static void createAndPrintNetworkPacket(String data, int srcPort, int dstPort, String envioEmisor, int SequenceNumber, int ackNum, String flag, int windowLength) {
        String status = "Succeed";
        String emisor = envioEmisor;
        String flags = flag;
        int seqNumber = 0;
        int ackNumber = 0;
        seqNumber = SequenceNumber;
        ackNumber = ackNum ;
        int len = data.length();
        int window = (windowLength != 0) ? windowLength :  65535 ;

        // Crear un nuevo paquete de red y mostrarlo
        NetworkPacket packet = new NetworkPacket(packetIndex++, status, emisor, srcPort, dstPort, flags, seqNumber, ackNumber, len, window, data);
        packet.printPacket();
    }
    // Se hace el Handshake Inicial 
    public static int[] threewayhandshake(int srcPort, int dstPort, int window, int seqNum, int ackNum ,String Sender, String Reciber){

        createAndPrintNetworkPacket("", srcPort, dstPort, "Client",seqNum,0,"SYN",0);
        seqNum++;
        createAndPrintNetworkPacket("", dstPort, srcPort, "Server",ackNum,seqNum,"SYN + ACK",0);
        ackNum++;
        createAndPrintNetworkPacket("", srcPort, dstPort, "Client",seqNum,ackNum,"ACK",window);
        int[] numerosackyseq={ackNum,seqNum};
        return numerosackyseq;

    }
    //Se hace el Handshake Final
    public static void fourwayHandshake(int srcPort, int dstPort, int window, int seqNum, int ackNum,String Sender, String Reciber){
        createAndPrintNetworkPacket("", srcPort, dstPort, "Client",seqNum,ackNum,"FIN + ACK",window);
        seqNum++;
        createAndPrintNetworkPacket("", dstPort, srcPort, "Server",ackNum,seqNum,"ACK",window);
        
        createAndPrintNetworkPacket("", srcPort, dstPort, "Server",ackNum,seqNum,"FIN + ACK",window);
        ackNum++;
        createAndPrintNetworkPacket("", dstPort, srcPort, "Client",seqNum,ackNum,"ACK",window);
        
    }
    // Simula cuando solo un lado envia datos
    public static void clientSimulation(List<String> quijoteParts, List<String> mosqueterosParts, double lossProbability, int windowSize, String Sender, String Reciber,String Data) {
        int quijoteIndex = 0;
        int serverIndex = 0;
        Random rand = new Random();

        // Imprimir encabezado de la tabla
        System.out.printf("%-5s %-8s %-10s %-8s %-8s %-19s %-12s %-12s %-4s %-6s %-6s%n",
                "Index", "Status", "Emisor", "SrcPort", "DstPort", "Flags", "SeqNumber", "AckNumber", "Len", "Window", "Datos");
        int srcPort = rand.nextInt(900) + 100 ; // Puerto del cliente 
        int dstPort = 80; // Puero del servidor 
        int seqNumber = rand.nextInt(200001) + 100000; ;
        int ackNumber =  rand.nextInt(200001) + 500000; 
        int[] threewayhandshakeNums = threewayhandshake(srcPort, dstPort, windowSize,seqNumber,ackNumber,Sender,Reciber);
        int newSeqNumber = threewayhandshakeNums[0];
        int newAckNumber = threewayhandshakeNums[1];
        System.out.println("Connection established");

        createAndPrintNetworkPacket("", srcPort, dstPort, Sender,newAckNumber,newSeqNumber, "TCP Window Update",windowSize);

        List<String> dataParts = null;

        if(Data.equals("Quijote")){
            dataParts = quijoteParts;
        }
        if(Data.equals("Mosqueteros")){
            dataParts = mosqueterosParts;
        }


        while (quijoteIndex < dataParts.size()) {
            for (int i = 0; i < windowSize && quijoteIndex < dataParts.size(); i++) {
                String quijoteFragment = dataParts.get(quijoteIndex);
                // Simular envío de paquete
                if (!sendPacket(quijoteFragment, lossProbability)) {
                    System.out.println("Reenviando fragmento: " + quijoteFragment);
                    sendPacket(quijoteFragment, lossProbability);
                }
                
                try {
                    if( quijoteFragment.length() < windowSize){
                       // Agregar un valor si el siguiente y el actual hacen lo de o menos la window
                       if(quijoteFragment.length() + dataParts.get(quijoteIndex+1).length() <= windowSize){
                        System.out.println("Tamnio " +quijoteFragment.length() + dataParts.get(quijoteIndex+1).length() );
                        System.out.println("Unirlos");

                        createAndPrintNetworkPacket(quijoteFragment, srcPort, dstPort, Sender,newSeqNumber,newAckNumber, "PSH + ACK",windowSize);
                        createAndPrintNetworkPacket(dataParts.get(quijoteIndex+1), srcPort, dstPort, Sender,newSeqNumber,newAckNumber, "PSH + ACK",windowSize);
                        newAckNumber = newAckNumber+quijoteFragment.length() + dataParts.get(quijoteIndex+1).length();
                        createAndPrintNetworkPacket("", dstPort, srcPort, Reciber,newAckNumber,newSeqNumber, "ACK",windowSize);
                        quijoteIndex++;
                        System.out.println(dataParts.get(quijoteIndex+1).length());
                        System.out.println("Agregarle data si es posible");
                       }
                       
                   }
                    
                } catch (Exception e) {
                    // TODO: handle exception
                   
                }
                if(quijoteFragment.length() > windowSize){
                    //Splitearlo por el tamaño maximo y mandarlo
                    //System.out.println("Hacer algo con la data osea fragmentarla de nuevo");
                    List<String> newFragmentQuijote = splitText(quijoteFragment,windowSize, windowSize);
                    
                    for (String subfrag : newFragmentQuijote) {
                        if (!sendPacket(subfrag, lossProbability)) {
                            System.out.println("Reenviando fragmento: " + subfrag);
                            sendPacket(subfrag, lossProbability);
                        }
                        createAndPrintNetworkPacket(subfrag, srcPort, dstPort, Sender,newSeqNumber,newAckNumber, "PSH + ACK",windowSize);
                        newAckNumber = newAckNumber+quijoteFragment.length();
                        createAndPrintNetworkPacket("", dstPort, srcPort, Reciber,newAckNumber,newSeqNumber, "ACK",windowSize);
                    }
                    
                }else{

                    createAndPrintNetworkPacket(quijoteFragment, srcPort, dstPort, Sender,newSeqNumber,newAckNumber, "PSH + ACK",windowSize);
                    newAckNumber = newAckNumber+quijoteFragment.length();
                    createAndPrintNetworkPacket("", dstPort, srcPort, Reciber,newAckNumber,newSeqNumber, "ACK",windowSize);
                }


                quijoteIndex++;
                serverIndex++;
            }
        }
        System.out.println("Two or Four way Handshake");
        fourwayHandshake(srcPort, dstPort, windowSize,newSeqNumber,newAckNumber,Sender,Reciber);
    }
    // Simula el envio de datos Esocgi una tecnica de cola Primero envia uno y luego el otro1
    public static void clientServerSimulation(List<String> quijoteParts, List<String> mosqueterosParts, double lossProbability, int windowSize, String Sender, String Reciber,String Data) {
        int quijoteIndex = 0;
        int serverIndex = 0;
        Random rand = new Random();

        // Imprimir encabezado de la tabla
        System.out.printf("%-5s %-8s %-10s %-8s %-8s %-19s %-12s %-12s %-4s %-6s %-6s%n",
                "Index", "Status", "Emisor", "SrcPort", "DstPort", "Flags", "SeqNumber", "AckNumber", "Len", "Window", "Datos");
        int srcPort = rand.nextInt(900) + 100 ; 
        int dstPort = 80; 
        int seqNumber = rand.nextInt(200001) + 100000; ;
        int ackNumber =  rand.nextInt(200001) + 500000; 
        int[] threewayhandshakeNums = threewayhandshake(srcPort, dstPort, windowSize,seqNumber,ackNumber,Sender,Reciber);
        int newSeqNumber = threewayhandshakeNums[0];
        int newAckNumber = threewayhandshakeNums[1];
        System.out.println("Connection established");

        createAndPrintNetworkPacket("", srcPort, dstPort, Sender,newAckNumber,newSeqNumber, "TCP Window Update",windowSize);

        List<String> dataParts = null;

        if(Data.equals("Quijote")){
            dataParts = quijoteParts;
        }
        if(Data.equals("Mosqueteros")){
            dataParts = mosqueterosParts;
        }


        while (quijoteIndex < dataParts.size()) {
            for (int i = 0; i < windowSize && quijoteIndex < dataParts.size(); i++) {
                String quijoteFragment = dataParts.get(quijoteIndex);
                // Simular envío de paquete
                if (!sendPacket(quijoteFragment, lossProbability)) {
                    System.out.println("Reenviando fragmento: " + quijoteFragment);
                    sendPacket(quijoteFragment, lossProbability);
                }
                
                try {
                    if( quijoteFragment.length() < windowSize){
                       // Agregar un valor si el siguiente y el actual hacen lo de o menos la window
                       if(quijoteFragment.length() + dataParts.get(quijoteIndex+1).length() <= windowSize){
                        System.out.println("Tamnio " +quijoteFragment.length() + dataParts.get(quijoteIndex+1).length() );
                        System.out.println("Unirlos");

                        createAndPrintNetworkPacket(quijoteFragment, srcPort, dstPort, Sender,newSeqNumber,newAckNumber, "PSH + ACK",windowSize);
                        createAndPrintNetworkPacket(dataParts.get(quijoteIndex+1), srcPort, dstPort, Sender,newSeqNumber,newAckNumber, "PSH + ACK",windowSize);
                        newAckNumber = newAckNumber+quijoteFragment.length() + dataParts.get(quijoteIndex+1).length();
                        createAndPrintNetworkPacket("", dstPort, srcPort, Reciber,newAckNumber,newSeqNumber, "ACK",windowSize);
                        quijoteIndex++;
                        System.out.println(dataParts.get(quijoteIndex+1).length());
                        System.out.println("Agregarle data si es posible");
                       }
                       
                   }
                    
                } catch (Exception e) {
                    // TODO: handle exception
                   
                }
                if(quijoteFragment.length() > windowSize){
                    //Splitearlo por el tamaño maximo y mandarlo
                    //System.out.println("Hacer algo con la data osea fragmentarla de nuevo");
                    List<String> newFragmentQuijote = splitText(quijoteFragment,windowSize, windowSize);
                    
                    for (String subfrag : newFragmentQuijote) {
                        if (!sendPacket(subfrag, lossProbability)) {
                            System.out.println("Reenviando fragmento: " + subfrag);
                            sendPacket(subfrag, lossProbability);
                        }
                        createAndPrintNetworkPacket(subfrag, srcPort, dstPort, Sender,newSeqNumber,newAckNumber, "PSH + ACK",windowSize);
                        newAckNumber = newAckNumber+quijoteFragment.length();
                        createAndPrintNetworkPacket("", dstPort, srcPort, Reciber,newAckNumber,newSeqNumber, "ACK",windowSize);
                    }
                    
                }else{

                    createAndPrintNetworkPacket(quijoteFragment, srcPort, dstPort, Sender,newSeqNumber,newAckNumber, "PSH + ACK",windowSize);
                    newAckNumber = newAckNumber+quijoteFragment.length();
                    createAndPrintNetworkPacket("", dstPort, srcPort, Reciber,newAckNumber,newSeqNumber, "ACK",windowSize);
                }


                quijoteIndex++;
                serverIndex++;
            }
        }
        if(Data.equals("Quijote")){
            dataParts = mosqueterosParts;
        }
        if(Data.equals("Mosqueteros")){
            dataParts = quijoteParts;
        }

        quijoteIndex =0;
        while (quijoteIndex < dataParts.size()) {
            for (int i = 0; i < windowSize && quijoteIndex < dataParts.size(); i++) {
                String quijoteFragment = dataParts.get(quijoteIndex);
                // Simular envío de paquete
                if (!sendPacket(quijoteFragment, lossProbability)) {
                    System.out.println("Reenviando fragmento: " + quijoteFragment);
                    sendPacket(quijoteFragment, lossProbability);
                }
                
                try {
                    if( quijoteFragment.length() < windowSize){
                       // Agregar un valor si el siguiente y el actual hacen lo de o menos la window
                       if(quijoteFragment.length() + dataParts.get(quijoteIndex+1).length() <= windowSize){
                        System.out.println("Tamnio " +quijoteFragment.length() + dataParts.get(quijoteIndex+1).length() );
                        System.out.println("Unirlos");

                        createAndPrintNetworkPacket(quijoteFragment, dstPort, srcPort, Reciber,newSeqNumber,newAckNumber, "PSH + ACK",windowSize);
                        createAndPrintNetworkPacket(dataParts.get(quijoteIndex+1), dstPort, srcPort, Reciber,newSeqNumber,newAckNumber, "PSH + ACK",windowSize);
                        newAckNumber = newAckNumber+quijoteFragment.length() + dataParts.get(quijoteIndex+1).length();
                        createAndPrintNetworkPacket("", srcPort, dstPort, Sender,newAckNumber,newSeqNumber, "ACK",windowSize);
                        quijoteIndex++;
                        System.out.println(dataParts.get(quijoteIndex+1).length());
                        System.out.println("Agregarle data si es posible");
                       }
                       
                   }
                    
                } catch (Exception e) {
                    // TODO: handle exception
                   
                }
                if(quijoteFragment.length() > windowSize){
                    //Splitearlo por el tamaño maximo y mandarlo
                    //System.out.println("Hacer algo con la data osea fragmentarla de nuevo");
                    List<String> newFragmentQuijote = splitText(quijoteFragment,windowSize, windowSize);
                    
                    for (String subfrag : newFragmentQuijote) {
                        if (!sendPacket(subfrag, lossProbability)) {
                            System.out.println("Reenviando fragmento: " + subfrag);
                            sendPacket(subfrag, lossProbability);
                        }
                        createAndPrintNetworkPacket(subfrag, dstPort, srcPort, Reciber,newSeqNumber,newAckNumber, "PSH + ACK",windowSize);
                        newAckNumber = newAckNumber+quijoteFragment.length();
                        createAndPrintNetworkPacket("", srcPort, dstPort, Sender,newAckNumber,newSeqNumber, "ACK",windowSize);
                    }
                    
                }else{

                    createAndPrintNetworkPacket(quijoteFragment, dstPort, srcPort,Reciber,newSeqNumber,newAckNumber, "PSH + ACK",windowSize);
                    newAckNumber = newAckNumber+quijoteFragment.length();
                    createAndPrintNetworkPacket("", srcPort, dstPort, Sender,newAckNumber,newSeqNumber, "ACK",windowSize);
                }


                quijoteIndex++;
                serverIndex++;
            }
        }

        System.out.println("Two or Four way Handshake");
        fourwayHandshake(srcPort, dstPort, windowSize,newSeqNumber,newAckNumber,Sender,Reciber);
    }

    public static void main(String[] args) {
        String quijote = "En un lugar de la Mancha, de cuyo nombre no quiero acordarme...";
        String mosqueteros = "Los tres mosqueteros eran conocidos en todo París por su valor...";


        Scanner scanner = new Scanner(System.in);
        while (true) {
            List<String> quijoteParts = splitText(quijote, 3, 12);
            List<String> mosqueterosParts = splitText(mosqueteros, 2, 12);
    
            int windowSize = calculateMode(quijoteParts, mosqueterosParts);
            System.out.println("Tamaño de ventana basado en la moda de las longitudes: " + windowSize);
            double lossProbability = 0.1;
            // Imprimir el menú
            System.out.println("Menú:");
            System.out.println("1. Enviar Quijote (Cliente(Envia)  / Servidor)");
            System.out.println("2. Enviar Mosquetero ( Servidor(Envia) / Cliente)");
            System.out.println("3. Envian ambos");
            System.out.println("4. Salir");
            System.out.print("Selecciona una opción: ");
            int opcion = scanner.nextInt();
            switch (opcion) {
                case 1:
                    clientSimulation(quijoteParts, mosqueterosParts, lossProbability, windowSize, "Client", "Server","Quijote");
                    break;
                case 2:
                    clientSimulation(quijoteParts, mosqueterosParts, lossProbability, windowSize, "Server", "Client","Mosqueteros");
                    break;
                case 3:

                    clientServerSimulation(quijoteParts, mosqueterosParts, lossProbability, windowSize, "Client", "Server","Quijote");

                    break;
                case 4:
                    System.out.println("Saliendo del programa...");
                    scanner.close(); // Cerrar el scanner
                    System.exit(0);  // Salir del programa
                default:
                    System.out.println("Opción inválida, por favor intenta de nuevo.");
            }


        }

    }
}
