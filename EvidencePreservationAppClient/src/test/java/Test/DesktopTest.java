package Test;

/**
 * Created by WangMingming on 2017/4/5.
 */
public class DesktopTest {
    public static void main(String args[]){
        if(java.awt.Desktop.isDesktopSupported()){
            try {
                java.net.URI uri = java.net.URI.create("http://www.baidu.com/");
                java.awt.Desktop dp = java.awt.Desktop.getDesktop();
                if(dp.isSupported(java.awt.Desktop.Action.BROWSE)){
                    dp.browse(uri);
                }
            } catch(java.lang.NullPointerException e){
                e.printStackTrace();
            } catch (java.io.IOException ei) {
                ei.printStackTrace();
            }
        }
    }

}
