/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Sewa_Villa;

/**
 *
 * @author DzulF
 */
import java.text.DecimalFormat;

public class Pembayaran extends Pemesanan implements FormatRupiah {
    double durasi, harga;
    private String Kodebayar;
    
    public Pembayaran() {
        this.Kodebayar = Kodebayar;
    }
    
    @Override //overriding
    //dapat melempar NumberFormatException
    public double hargaSewa() throws NumberFormatException  
    {
        return super.hargaSewa(harga, durasi); 
    }
    
    public String getKodebayar() {
            return Kodebayar;
        }
        
    public void setKodebayar(String Kodebayar) {
        this.Kodebayar = Kodebayar;
    }

    @Override
    public String formatBiaya(double biaya) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00");
        return decimalFormat.format(biaya);
    }    
}
