package controlador;

public interface Comunicar {
    void comunicar(String args);

    default void encriptar(int id, String filePath, String outPath, boolean comprimir){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    default void desencriptar(int id, String filePath, String outPath){
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
