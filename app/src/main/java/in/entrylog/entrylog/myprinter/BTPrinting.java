package in.entrylog.entrylog.myprinter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import in.entrylog.entrylog.values.DetailsValue;

/**
 * Created by Admin on 16-Jun-16.
 */
public class BTPrinting extends IO {
    private static final String TAG = "BTPrinting";
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothSocket s = null;
    public InputStream is = null;
    public OutputStream os = null;
    private boolean isOpened = false;
    DetailsValue details;
    BluetoothDevice device;
    static String task;

    public BTPrinting() {
    }

    public boolean Open(String BTAddress) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            return false;
        } else {
            boolean valid = false;
            device = bluetoothAdapter.getRemoteDevice(BTAddress);

            try {
                this.s = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException var9) {
                ;
            }

            bluetoothAdapter.cancelDiscovery();

            try {
                this.s.connect();
                this.os = this.s.getOutputStream();
                this.is = this.s.getInputStream();
                valid = true;
                this.task = "Chetan";
                /*Log.v("BTRWThread OpenOfficial", "Connected to " + BTAddress);*/
            } catch (IOException var8) {
                try {
                    this.s.close();
                } catch (IOException var7) {
                    ;
                }
            }

            if(valid) {
                this.isOpened = true;
            } else {
                this.isOpened = false;
                this.s = null;
            }

            this.CheckKCPrinter();
            return valid;
        }
    }

    public void Close() {
        try {
            if(this.is != null) {
                this.is.close();
                this.is = null;
            }

            if(this.os != null) {
                this.os.close();
                this.os = null;
            }

            if(this.s != null) {
                this.s.close();
                this.s = null;
            }

            Log.v("BTRWThread Close", "Close BluetoothSocket");
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        this.isOpened = false;
    }

    public boolean IsOpened() {
        return this.isOpened;
    }

    public int Write(byte[] buffer, int offset, int count) {
        if(this.os != null) {
            try {
                this.os.write(buffer, offset, count);
                this.os.flush();
                return count;
            } catch (IOException var5) {
                var5.printStackTrace();
            }
        }

        return -1;
    }

    public int Read(byte[] buffer, int offset, int count, int timeout) {
        int cnt = 0;
        boolean error = false;
        if(this.is != null) {
            try {
                long e = System.currentTimeMillis();

                while(System.currentTimeMillis() - e < (long)timeout && cnt != count) {
                    int available = this.is.available();
                    if(available > 0) {
                        int rec = this.is.read(buffer, offset + cnt, 1);
                        if(rec <= 0) {
                            error = true;
                            break;
                        }

                        Log.i("BTPrinting", "" + buffer[offset + cnt]);
                        cnt += rec;
                    } else {
                        Thread.sleep(10L);
                    }
                }
            } catch (Exception var11) {
                var11.printStackTrace();
                error = true;
            }
        }

        return error && cnt == 0?-1:cnt;
    }

    public OutputStream GetOutputStream() throws IOException {
        return this.s.getOutputStream();
    }

    public InputStream GetInputStream() throws IOException {
        return this.s.getInputStream();
    }

    public static String GetTask() {
        return task;
    }

    public static BluetoothSocket GetSocket() {
        return s;
    }
}
