package com.neoenergia.mom.apkinstall

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.datalogic.device.battery.BatteryInfo
import com.datalogic.device.battery.DLBatteryManager


class BatteryActivity : AppCompatActivity() {
    private var batteryStatus: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery)

        val batteryLevelFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        batteryStatus = registerReceiver(null, batteryLevelFilter)

        findViewById<TextView>(R.id.batteryTextView).text = """
            Battery Info: ${getBatteryInfo().toString()}
            Battery Status: ${getStatus().toString()}
            External AC Power: ${getExtPowerStatus().toString()}
            External USB Power: ${getUsbPowerStatus().toString()}
            Current level: ${getCurrentLevel().toString()} %
            
            """.trimIndent()
    }

    private fun getExtPowerStatus(): Boolean {
        val externalPowerSource: Int = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        return externalPowerSource == BatteryManager.BATTERY_PLUGGED_AC
    }

    private fun getUsbPowerStatus(): Boolean {
        val chargePlug: Int = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        return chargePlug == BatteryManager.BATTERY_PLUGGED_USB
    }
    private fun getBatteryInfo(): String? {
        val deviceBattery = DLBatteryManager.getInstance()
        return """
            capacity: ${deviceBattery.getIntProperty(BatteryInfo.CAPACITY_REMAINING)}
            year: ${deviceBattery.getIntProperty(BatteryInfo.PRODUCTION_YEAR)}
            month: ${deviceBattery.getIntProperty(BatteryInfo.PRODUCTION_MONTH)}
            serial_number: ${deviceBattery.getStringProperty(BatteryInfo.SERIAL_NUMBER)}
            manufacturer: ${deviceBattery.getStringProperty(BatteryInfo.MANUFACTURER)}
        """
    }
    private fun getStatus(): String? {
        val resultStatus: String
        val status: Int = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        resultStatus = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
            else -> "Unknown"
        }
        return resultStatus
    }
    fun getChargingStatus(): Boolean {
        val status: Int = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        return status == BatteryManager.BATTERY_STATUS_CHARGING
    }

    fun getDischargingStatus(): Boolean {
        val status: Int = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        return status == BatteryManager.BATTERY_STATUS_DISCHARGING
    }

    private fun getCurrentLevel(): Float {
        val level: Int = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale: Int = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        return level * 100 / scale.toFloat()
    }
}