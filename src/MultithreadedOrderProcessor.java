package src;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class OrderProcessor implements Runnable {
    private int orderId;

    public OrderProcessor(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public void run() {
        // Buat koneksi sendiri untuk setiap thread
        String URL = "jdbc:mysql://localhost:3306/toko_online?autoReconnect=true&useSSL=false";
        String USER = "root";
        String PASS = "";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            // Update status menjadi "Diproses"
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE orders SET status = ? WHERE id = ?");
            ps.setString(1, "Diproses");
            ps.setInt(2, orderId);
            ps.executeUpdate();

            System.out.println("Pesanan ID " + orderId + " sedang diproses oleh " 
                                + Thread.currentThread().getName());

            // Simulasi waktu pemrosesan
            Thread.sleep((int)(Math.random() * 2000 + 2000));

            // Update status menjadi "Selesai"
            ps = conn.prepareStatement(
                "UPDATE orders SET status = ? WHERE id = ?");
            ps.setString(1, "Selesai");
            ps.setInt(2, orderId);
            ps.executeUpdate();

            System.out.println("Pesanan ID " + orderId + " telah selesai oleh " 
                                + Thread.currentThread().getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


 public class MultithreadedOrderProcessor {
    private static final String URL = "jdbc:mysql://localhost:3306/toko_online";
    private static final String USER = "root";
    private static final String PASS = "";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.println("Koneksi ke database berhasil.");

            PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM orders WHERE status = 'Menunggu'");
            ResultSet rs = ps.executeQuery();

            ExecutorService executor = Executors.newFixedThreadPool(3);

            while (rs.next()) {
                int id = rs.getInt("id");
                executor.execute(new OrderProcessor(id)); // koneksi dibuat di dalam thread
            }

            executor.shutdown();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

