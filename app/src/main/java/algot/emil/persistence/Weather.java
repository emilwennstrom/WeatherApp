package algot.emil.persistence;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Weather {

    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "temperature_2m_max")
    public float temperature;


    @ColumnInfo(name = "rain_sum")
    public String rainSum;

    @ColumnInfo(name = "showers_sum")
    public String showersSum;

    @ColumnInfo(name = "snowfall_sum")
    public String snowfallSum;




}
