
package com.nanl.companion;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {

    private LinearLayout layout;
    private TextView permissionStatus;
    private TextView usageResult;

    private int pink = Color.rgb(255, 120, 189);
    private int background = Color.rgb(18, 17, 26);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createInterface();
        updateStatus();
    }

    private void createInterface() {
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(28, 35, 28, 25);
        layout.setBackgroundColor(background);

        TextView title = new TextView(this);
        title.setText("NanL Companion");
        title.setTextSize(27);
        title.setTextColor(pink);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 20, 0, 10);
        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("PARENTAL CONTROL COMPANION");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setTextSize(12);
        layout.addView(subtitle);

        addSpace();

        TextView information = new TextView(this);
        information.setText(
            "Aplikasi pendamping keluarga.\n\n" +
            "Aplikasi ini dapat membaca ringkasan " +
            "penggunaan aplikasi setelah izin diberikan " +
            "secara manual oleh pengguna perangkat."
        );
        information.setTextColor(Color.WHITE);
        information.setTextSize(15);
        information.setPadding(15, 20, 15, 20);
        information.setBackgroundColor(Color.rgb(40, 32, 45));
        layout.addView(information);

        addSpace();

        permissionStatus = new TextView(this);
        permissionStatus.setTextColor(pink);
        permissionStatus.setTextSize(15);
        layout.addView(permissionStatus);

        Button permissionButton = new Button(this);
        permissionButton.setText("Atur Izin Penggunaan");
        permissionButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                Settings.ACTION_USAGE_ACCESS_SETTINGS
            );
            startActivity(intent);
        });
        layout.addView(permissionButton);

        addSpace();

        Button reportButton = new Button(this);
        reportButton.setText("Lihat Ringkasan Penggunaan");
        reportButton.setOnClickListener(v -> showUsage());
        layout.addView(reportButton);

        addSpace();

        usageResult = new TextView(this);
        usageResult.setTextColor(Color.WHITE);
        usageResult.setTextSize(14);
        layout.addView(usageResult);

        addSpace();

        TextView footer = new TextView(this);
        footer.setText(
            "Pemantauan transparan • NanL Control\n" +
            "Made with love by NanL"
        );
        footer.setTextColor(Color.GRAY);
        footer.setGravity(Gravity.CENTER);
        footer.setTextSize(12);
        layout.addView(footer);

        setContentView(layout);
    }

    private void addSpace() {
        View space = new View(this);
        layout.addView(space, new LinearLayout.LayoutParams(
            1, 25
        ));
    }

    private boolean hasUsagePermission() {
        AppOpsManager manager =
            (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);

        int result = manager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            getPackageName()
        );

        return result == AppOpsManager.MODE_ALLOWED;
    }

    private void updateStatus() {
        if (hasUsagePermission()) {
            permissionStatus.setText(
                "● Izin penggunaan sudah aktif"
            );
        } else {
            permissionStatus.setText(
                "○ Izin penggunaan belum diberikan"
            );
        }
    }

    private void showUsage() {
        if (!hasUsagePermission()) {
            usageResult.setText(
                "Aktifkan izin penggunaan terlebih dahulu."
            );
            return;
        }

        UsageStatsManager manager =
            (UsageStatsManager) getSystemService(
                Context.USAGE_STATS_SERVICE
            );

        Calendar calendar = Calendar.getInstance();
        calendar.set(
            Calendar.HOUR_OF_DAY, 0
        );
        calendar.set(
            Calendar.MINUTE, 0
        );
        calendar.set(
            Calendar.SECOND, 0
        );
        calendar.set(
            Calendar.MILLISECOND, 0
        );

        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();

        List<UsageStats> stats = manager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            start,
            end
        );

        if (stats == null || stats.isEmpty()) {
            usageResult.setText(
                "Belum ada data penggunaan yang tersedia."
            );
            return;
        }

        long total = 0;

        for (UsageStats item : stats) {
            total += item.getTotalTimeInForeground();
        }

        long minutes = total / 60000;
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        usageResult.setText(
            "Ringkasan penggunaan hari ini:\n\n" +
            "Total: " + hours + " jam " +
            remainingMinutes + " menit\n\n" +
            "Data hanya ditampilkan di perangkat ini."
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (permissionStatus != null) {
            updateStatus();
        }
    }
}

