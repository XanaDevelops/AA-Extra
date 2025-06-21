package model.Huffman;


import model.BitsManagement.BitOutputStream;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Compressor {
    private final Huffman huffman;
    private final String inputPath;
    private final String outputFile;

    private File outFile;

    private int id;

    public Compressor(int id, String inputPath, String outputFile) {
        this(new Huffman(inputPath),  inputPath, outputFile);
        this.id = id;
    }
    public Compressor(Huffman huffman, String inputPath, String outputFile) {
        this.huffman = huffman;

        this.inputPath = inputPath;
        this.outputFile = outputFile;

        this.id = 0;
    }

    /**
     * Comprimeix el fitxer d'entrada.
     * La capçalera té la següent forma
     * (Byte) --> nombre de caracters únics de l'arxiu
     * Conjunt de bytes (Simbol, longitud) --> forma canònica de la codificació de huffman
     * A baix de la capçalera s'escriu el contingut codificat de huffman
     *
     * @throws IOException
     */

    public void compressFile() throws IOException {
        System.err.println("comprimint " + inputPath + " to " + outputFile);
        System.err.println("settings bytes: " + huffman.getByteSize());

        huffman.run();
        Map<Long, String> table = huffman.getTable();
        long time = System.nanoTime();
        //calcular la longitud de les codificaciones de cada byte
        Map<Long, Integer> codeLengths = new TreeMap<>();
        int totalUnicSymbols = 0;
        List<Long> symbols = new ArrayList<>();
        for (Map.Entry<Long, String> e : table.entrySet()) {
            long sym = e.getKey(); //byte positiu
            codeLengths.put(sym, e.getValue().length());
            symbols.add(sym);
            totalUnicSymbols++;
        }
        //generar codi canònic a partir les longituds dels símbols
        Map<Long, byte[]> canonCodes = Huffman.generateCanonicalCodes(codeLengths, symbols);

        //afegir la signatura de l'extensió manualment


        String fileName = Path.of(outputFile).getFileName().toString();

        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        outFile = new File(new File(outputFile).getParent() + "/" + fileName + HuffHeader.EXTENSIO);
        System.out.println("COM File: " + outFile);
        try (OutputStream fos = Files.newOutputStream(Path.of(outFile.getAbsolutePath()));
             BufferedOutputStream bufOut = new BufferedOutputStream(fos);
             DataOutputStream dos = new DataOutputStream(bufOut);
             BitOutputStream bitOut = new BitOutputStream(bufOut)) {

            /// HEADER
            //guardar l'extensió original de l'arxiu
            dos.write(HuffHeader.magicNumbers);

            //guardar el tamany de les paraules comprimides
            dos.writeShort(huffman.getByteSize());

            Path inputPath = Path.of(this.inputPath);
            String[] extension = this.inputPath.split("\\.", 2);
            byte[] extensionBytes = extension[1].getBytes(StandardCharsets.UTF_8);
            //tamany de l'extensió
            dos.writeShort(extensionBytes.length);
            //l'extensió en sí
            dos.write(extensionBytes);

            dos.writeInt(totalUnicSymbols);
            // llista de (simbol, longitud)
            for (Map.Entry<Long, Integer> e : codeLengths.entrySet()) {
                dos.writeLong(e.getKey());
                dos.writeInt(e.getValue());
            }

            long originalBytes = Files.size(inputPath);
            dos.writeLong(originalBytes);
            dos.flush();

            //Escriure la codificació del contingut de l'arxiu d'entrada
            try (InputStream fis = new BufferedInputStream(Files.newInputStream(inputPath))) {
                long b;
                while ((b = fis.read()) != -1) {
                    for (int j = 1; j < huffman.getByteSize(); j++) {
                        b = b<<8;
                        int aux = fis.read();
                        b |= aux != -1 ? aux : 0;
                    }

                    byte[] codeBits = canonCodes.get(b);
                    assert codeBits != null;
                    for (byte codeBit : codeBits) {
                        bitOut.writeBit(codeBit == 1);
                    }

                }
            }
            bitOut.flush();
        }


        time = System.nanoTime() - time;
        System.err.println("end " + id);
//        data.addTempsCompressio(time, fileName, huffman.getTipusCua);
    }

    public File getFileOut() {
        return outFile;
    }




}