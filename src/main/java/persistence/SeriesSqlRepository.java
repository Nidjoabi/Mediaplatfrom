package persistence;

import Modules.Series;

import java.util.ArrayList;
import java.util.List;

public class SeriesSqlRepository implements ISeriesRepository {

    private static final SeriesSqlRepository instance = new SeriesSqlRepository();
    public static SeriesSqlRepository getInstance(){return instance;}

    private final List<Series> serieslist;
    public SeriesSqlRepository() { serieslist = new ArrayList<>();}


    @Override
    public Series addSeries(Series series) {
        if (series == null) {
            System.out.println("Series is null");
        }
        serieslist.add(series);
        return series;
    }
}
