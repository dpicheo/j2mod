/*
 * Copyright 2002-2016 jamod & j2mod development teams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.j2mod.modbus.cmd;

import com.j2mod.modbus.io.ModbusTransaction;
import com.j2mod.modbus.io.ModbusTransport;
import com.j2mod.modbus.msg.MaskWriteRegisterRequest;
import com.j2mod.modbus.msg.ModbusRequest;
import com.j2mod.modbus.net.ModbusMasterFactory;
import com.j2mod.modbus.util.ModbusLogger;

import java.io.IOException;

/**
 * Class that implements a simple command line tool for writing to an analog
 * output over a Modbus/TCP connection.
 *
 * <p>
 * Note that if you write to a remote I/O with a Modbus protocol stack, it will
 * most likely expect that the communication is <i>kept alive</i> after the
 * first write message.
 *
 * <p>
 * This can be achieved either by sending any kind of message, or by repeating
 * the write message within a given period of time.
 *
 * <p>
 * If the time period is exceeded, then the device might react by turning off
 * all signals of the I/O modules. After this timeout, the device might require
 * a reset message.
 *
 * @author Dieter Wimberger
 * @author jfhaugh
 * @version @version@ (@date@)
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public class MaskWriteRegisterTest {

    private static final ModbusLogger logger = ModbusLogger.getLogger(MaskWriteRegisterTest.class);

    private static void printUsage() {
        logger.system("\nUsage:\n    java com.j2mod.modbus.cmd.WriteHoldingRegisterTest <address{:<port>{:<unit>}} [String]> <register [int]> <andMask [int]> <orMask [int]> {<repeat [int]>}");
    }

    public static void main(String[] args) {

        ModbusTransport transport = null;
        ModbusRequest req;
        ModbusTransaction trans;
        int ref = 0;
        int andMask = 0xFFFF;
        int orMask = 0;
        int repeat = 1;
        int unit = 0;

        // 1. Setup parameters
        if (args.length < 3) {
            printUsage();
            System.exit(1);
        }

        try {
            try {
                ref = Integer.parseInt(args[1]);
                andMask = Integer.parseInt(args[2]);
                orMask = Integer.parseInt(args[3]);

                if (args.length == 5) {
                    repeat = Integer.parseInt(args[4]);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                printUsage();
                System.exit(1);
            }

            // 2. Open the connection
            transport = ModbusMasterFactory.createModbusMaster(args[0]);
            logger.system("Connected to %s", transport);
            req = new MaskWriteRegisterRequest(ref, andMask, orMask);
            req.setUnitID(unit);
            logger.system("Request: %s", req.getHexMessage());

            // 3. Prepare the transaction
            trans = transport.createTransaction();
            trans.setRequest(req);

            // 4. Execute the transaction repeat times

            for (int count = 0; count < repeat; count++) {
                trans.execute();
                if (trans.getResponse() != null) {
                    logger.system("Response: %s", trans.getResponse().getHexMessage());
                }
                else {
                    logger.system("No response");
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                if (transport != null) {
                    transport.close();
                }
            }
            catch (IOException e) {
                // Do nothing.
            }
        }
    }
}
