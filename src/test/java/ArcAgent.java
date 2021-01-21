import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino2sql.Action;
import com.lwjhn.domino2sql.ArcConfigSerialization;
import com.lwjhn.domino2sql.config.ArcConfig;
import lotus.domino.*;
import java.io.File;

public class ArcAgent extends AgentBase {
    final String configpath = "D:/EX_NPXC/arc.sql.config.json";
    final String output = "D:/EX_NPXC/arc.sql.output.json";
    Session session = null;
    ArcConfig config = null;
    Action action = null;
    public void NotesMain() {
        try {
            System.out.println("ArchXC Agent Start . ");
            config = ArcConfigSerialization.parseArcConfig(new File(configpath));
            Action action = new Action(config,session = getSession());
            action.archive();
            action.recycle();
            action=null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(config!=null){
                    ArcConfigSerialization.toJSONFile(config,output);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            BaseUtils.recycle(action, session);
            System.out.println("ArchXC Agent ShutDown . ");
        }
    }
}