<?php
// Konfigurasi Database (Sesuaikan dengan XAMPP kamu)
$host = "localhost";
$user = "root";
$pass = "";
$db   = "sentragizi_db";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die("Koneksi Gagal: " . $conn->connect_error);
}

// Ambil ID dari URL (contoh: index.php?id=BATCH-123)
$batch_id = isset($_GET['id']) ? $_GET['id'] : '';

// Query Data Inspeksi (Join dengan data Menu & Vendor biar lengkap)
$sql = "SELECT i.*, m.name as menu_name, v.name as vendor_name 
        FROM inspections i
        LEFT JOIN menus m ON i.menu_id = m.id
        LEFT JOIN vendors v ON i.vendor_id = v.id
        WHERE i.batch_uuid = '$batch_id'";

$result = $conn->query($sql);
$data = $result->fetch_assoc();
?>

<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Paspor Mutu Pangan - SentraGizi</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f9; display: flex; justify-content: center; padding: 20px; }
        .card { background: white; width: 100%; max-width: 400px; border-radius: 15px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); overflow: hidden; }
        .header { background: #2c3e50; color: white; padding: 20px; text-align: center; }
        .status-box { padding: 15px; text-align: center; font-weight: bold; font-size: 1.2em; }
        .status-approved { background-color: #27ae60; color: white; }
        .status-rejected { background-color: #c0392b; color: white; }
        .content { padding: 20px; }
        .row { display: flex; justify-content: space-between; margin-bottom: 10px; border-bottom: 1px solid #eee; padding-bottom: 5px; }
        .label { color: #7f8c8d; font-size: 0.9em; }
        .value { font-weight: 600; color: #2c3e50; }
        img.proof { width: 100%; border-radius: 10px; margin-top: 10px; border: 1px solid #ddd; }
        .footer { text-align: center; font-size: 0.8em; color: #aaa; padding: 20px; }
    </style>
</head>
<body>

    <?php if (!$data): ?>
        <div class="card">
            <div class="header" style="background: #e74c3c;">DATA TIDAK DITEMUKAN</div>
            <div class="content">
                <p style="text-align:center;">Maaf, data batch ini tidak terdaftar atau belum di-upload.</p>
            </div>
        </div>
    <?php else: ?>

        <div class="card">
            <div class="header">
                <h3>SENTRAGIZI MBG</h3>
                <small>Kartu Kendali Mutu Pangan</small>
            </div>

            <?php if ($data['final_status'] == 'APPROVED'): ?>
                <div class="status-box status-approved">✅ AMAN & LAYAK</div>
            <?php else: ?>
                <div class="status-box status-rejected">❌ DITOLAK</div>
            <?php endif; ?>

            <div class="content">
                <div style="text-align: center; margin-bottom: 20px;">
                    <img src="uploads/<?= $data['photo_path'] ?>" class="proof" alt="Bukti Foto">
                    <small style="color:#888;">Bukti Visual</small>
                </div>

                <div class="row">
                    <span class="label">Menu</span>
                    <span class="value"><?= $data['menu_name'] ?></span>
                </div>
                <div class="row">
                    <span class="label">Vendor Bahan</span>
                    <span class="value"><?= $data['vendor_name'] ?></span>
                </div>
                <div class="row">
                    <span class="label">Suhu Masak</span>
                    <span class="value"><?= $data['measured_temp'] ?> °C</span>
                </div>
                <div class="row">
                    <span class="label">Berat Porsi</span>
                    <span class="value"><?= $data['measured_weight'] ?> Gram</span>
                </div>
                <div class="row">
                    <span class="label">Waktu Inspeksi</span>
                    <span class="value"><?= date('d M Y, H:i', strtotime($data['inspection_time'])) ?></span>
                </div>
            </div>

            <div class="footer">
                ID Batch: <?= $data['batch_uuid'] ?><br>
                Terverifikasi oleh Sistem SentraGizi
            </div>
        </div>

    <?php endif; ?>

</body>
</html>