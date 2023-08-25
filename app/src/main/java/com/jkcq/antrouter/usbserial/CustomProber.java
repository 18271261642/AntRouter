package com.jkcq.antrouter.usbserial;

import com.jkcq.antrouter.usbserial.driver.CdcAcmSerialDriver;
import com.jkcq.antrouter.usbserial.driver.Ch34xSerialDriver;
import com.jkcq.antrouter.usbserial.driver.Cp21xxSerialDriver;
import com.jkcq.antrouter.usbserial.driver.FtdiSerialDriver;
import com.jkcq.antrouter.usbserial.driver.ProbeTable;
import com.jkcq.antrouter.usbserial.driver.ProlificSerialDriver;
import com.jkcq.antrouter.usbserial.driver.UsbSerialProber;

/**
 * Created by Admin
 * Date 2023/2/18
 */
public class CustomProber {

   public static UsbSerialProber getCustomProber() {
        ProbeTable customTable = new ProbeTable();
       // e.g. Digispark CDC
        customTable.addProduct(6421, 24857, CdcAcmSerialDriver.class);

       customTable.addProduct(1003,24857,CdcAcmSerialDriver.class);


        return new UsbSerialProber(customTable);
    }
}
