/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Sewa_Villa;

/**
 *
 * @author Dzuna
 */


public abstract class Pemesanan {
    private String nama;
    private String alamat;
    double totalSewa; 
    
    public Pemesanan() {
        this.nama = nama;
        this.alamat = alamat;
    }
    
    public abstract double hargaSewa();
    
    //overriding
    public double hargaSewa(double harga, double durasi){
        return totalSewa = harga * durasi;
    }
    
    public String getNama(){
        return nama;
    }
    
    public void setNama(String nama){
        this.nama = nama;
    }
    
    public String getAlamat(){
        return alamat;
    }
    
    public void setAlamat(String Alamat){
        this.alamat = Alamat;
    }
}
