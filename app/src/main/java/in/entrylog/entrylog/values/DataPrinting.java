package in.entrylog.entrylog.values;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Admin on 17-Aug-16.
 */
public class DataPrinting {
    FunctionCalls functionCalls = new FunctionCalls();

    public void SaveOrganization(String OrganizationName) {

        String path = functionCalls.filepath("Textfile");
        String filename = "Organization.txt";
        try {
            File f = new File(path + File.separator + filename);
            FileOutputStream fOut = new FileOutputStream(f);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            myOutWriter.append(OrganizationName + "\r\n");
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SaveHeader() {

        String path = functionCalls.filepath("Textfile");
        String filename = "Header.txt";
        try {
            File f = new File(path + File.separator + filename);
            FileOutputStream fOut = new FileOutputStream(f);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            myOutWriter.append("VISITOR" + "\r\n");
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SaveEmpty() {

        String path = functionCalls.filepath("Textfile");
        String filename = "Empty.txt";
        try {
            File f = new File(path + File.separator + filename);
            FileOutputStream fOut = new FileOutputStream(f);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            myOutWriter.append("        "+"\r\n");
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
