package bukkit.Clemens.torchmissile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertiesFile
{
  private static final Logger log = Logger.getLogger("Minecraft");
  private String fileName;
  public Properties props = new Properties();
  private FileInputStream inputStream;
  private FileOutputStream outputStream;

  public PropertiesFile(String fileName)
  {
    this.fileName = fileName;
  }

  public void load()
    throws IOException
  {
    this.inputStream = new FileInputStream(this.fileName);
    this.props.load(this.inputStream);
  }

  public void save()
  {
    try
    {
      this.outputStream = new FileOutputStream(this.fileName);
      this.props.store(this.outputStream, null);
    } catch (IOException ex) {
      log.severe("[PropertiesFile] Unable to save " + this.fileName + "!");
    }
  }

  public void close()
  {
    if (this.outputStream != null)
      try {
        this.outputStream.close();
      } catch (IOException e) {
        log.severe("[PropertiesFile] Failed to close " + this.fileName + " writer!");
      }
    else if (this.inputStream != null)
      try {
        this.inputStream.close();
      } catch (IOException e) {
        log.severe("[PropertiesFile] Failed to close " + this.fileName + " reader!");
      }
  }
  
  public boolean containsKey(String var)
  {
    return this.props.containsKey(var);
  }

  public void saveDefaultSettings()
  {
	this.props.setProperty("Torch_Spawn_Radius", "5");
	this.props.setProperty("Walls_Only", "true");
    this.save();
  }

}
