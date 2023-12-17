    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package Sewa_Villa;

    /**
     *
     * @author Dzuna
     */
    public class Villa {
        private String kodeVilla;
        String tipe;
        int jumlahKamarTidur;
        boolean adaKolamRenang;
        boolean adaRooftop;
        double hargaSewaPerMalam;

        public Villa(String kodeVilla, int jumlahKamarTidur, boolean adaKolamRenang, boolean adaRooftop, double hargaSewaPerMalam) {
            this.kodeVilla = kodeVilla;
            this.tipe = "Biasa";
            this.jumlahKamarTidur = jumlahKamarTidur;
            this.adaKolamRenang = adaKolamRenang;
            this.adaRooftop = adaRooftop;
            this.hargaSewaPerMalam = hargaSewaPerMalam;
        }

        // getter kodevilla
        public String getkodeVilla() {
            return kodeVilla;
        }

        //setter kodevilla
        public void setKodeVilla(String kodeVilla) {
            this.kodeVilla = kodeVilla;
        }   


        //objek villa
        public static Villa villaKecil = new Villa("VK", 4, false, false, 800000 );
        public static Villa villaSedang = new Villa("VS", 7, true, false, 2000000);
        public static Villa villaBesar = new Villa("VB", 9, true, true, 3500000);

        // Getter dan setter untuk atribut-atribut tersebut
        public double getHargaSewaPerMalam() {
            if (kodeVilla.equals("VK")) {
                return 800000.0; // Harga sewa villa kecil
            } else if (kodeVilla.equals("VS")) {
                return 2000000.0; // Harga sewa villa sedang
            } else if (kodeVilla.equals("VB")) {
                return 3500000.0; // Harga sewa villa besar
            } else {
                // Tipe villa tidak dikenali, Anda dapat menambahkan logika atau nilai default di sini
                return 0.0; // Nilai default jika tipe villa tidak dikenali
            }
        }

        // Metode  untuk cekStatus, getHargaSewa
        public String getDetailVilla() {
        String detail = "Tipe \t: " + tipe + "\n";
        detail += "Kamar Tidur\t: " + jumlahKamarTidur + "\n";
        detail += "Kolam Renang\t: " + (adaKolamRenang ? "Ya" : "Tidak") + "\n";
        detail += "Rooftop\t: " + (adaRooftop ? "Ya" : "Tidak") + "\n";
        detail += "Harga Sewa per Malam: Rp " + hargaSewaPerMalam + "\n"; // Menggunakan nilai dari atribut hargaSewaPerMalam
        return detail;
        }
    }

