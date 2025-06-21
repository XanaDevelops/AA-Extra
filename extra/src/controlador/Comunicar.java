package controlador;

import model.CryptHeader;

public interface Comunicar {
    void comunicar(String args);

    default void crearClau(int id, String nomClau, int n){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    default void eliminarClau(int idClau){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    default void encriptar(int id, String filePath, String outPath, boolean comprimir){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    default void desencriptar(int id, String filePath, String outPath) throws CryptHeader.InvalidKeyHeader {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    default void encriptar(int id, String kName, String filePath, String outPath, boolean comprimir){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    default void desencriptar(int id, String kName, boolean isAuto, String filePath, String outPath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    default void aturar(int id){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    default void finalitzar(int id){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    default void actualizar(){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    default void arrancar(int id){
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
