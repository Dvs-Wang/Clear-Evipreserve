package sample.Module;

import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * Created by WangMingming on 2017/4/3.
 */
public class Grape {
    public static Line ConnectedLine(double stx,double sty,double endx,double endy){
        Line line = new Line(stx,sty,endx,endy);
        line.setStroke(Color.GRAY);
        return line;
    }
    public static Circle EvidenceCircle(double dx,double dy,double radius,Evidence evidence){
        Circle circle = new Circle(dx,dy,radius,evidence.getIsConfirmed()?Color.GREEN : Color.RED);
        if(evidence.getTxHash().length()!=0 && evidence.getTxHash() != null){
            Tooltip.install(circle,new Tooltip("Evidence: " + evidence.getName() +  "\nIn tx: "+ evidence.getTxHash() + "\nIs Confirmed: " + evidence.getIsConfirmed()));
        }else if(evidence.getLightProof()!=null && evidence.getLightProof().getTxHash().length()!=0 && evidence.getLightProof().getTxHash()!=null) {
            Tooltip.install(circle,new Tooltip("Evidence: " + evidence.getName() +  "\nIn tx: "+ evidence.getLightProof().getTxHash() + "\nIs Confirmed: " + evidence.getIsConfirmed()));
        }
        return circle;
    }
}
