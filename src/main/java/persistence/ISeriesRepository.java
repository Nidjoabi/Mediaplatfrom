package persistence;

import Modules.Series;

public interface ISeriesRepository {
    Series addSeries(Series series, long userId);
    boolean deleteSeries(int mediaId, long userId);
    Series updateSeries(int mediaId, Series series, long userId);
    Series getSeriesById(int mediaId);
}
