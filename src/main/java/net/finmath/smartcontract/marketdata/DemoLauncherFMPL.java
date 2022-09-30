package net.finmath.smartcontract.marketdata;

import net.finmath.smartcontract.descriptor.xmlparser.FPMLParser;
import net.finmath.smartcontract.valuation.MarginCalculator;

import java.nio.file.Files;
import java.nio.file.Path;

public class DemoLauncherFMPL {

    public static void main(String[] args) throws Exception {

        String fpml = Files.readString(Path.of("C:\\sdc\\sdc-events\\src\\main\\deploy\\data\\ird-ex01-sample-swap.xml"));
        FPMLParser parser = new FPMLParser("party1", "discount-EUR-OIS","forward-EUR-6M", fpml);
        String fName1 = Files.readString(Path.of("C:\\Temp\\md_2022-09-22-12-53-01.json"));
        String fName2 = Files.readString(Path.of("C:\\Temp\\md_2022-09-22-11-50-03.json"));



        MarginCalculator calculator = new MarginCalculator();
        double margin = calculator.getValue(fName1,fName2,fpml);
        System.out.println(margin);

    }
}
