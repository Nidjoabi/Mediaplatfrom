package service;


import Modules.Game;
import Modules.Series;

public interface ISeriesService {
    Series addSeries(Series series, long userId);
    boolean deleteSeries(int mediaId, long userId);
    Series updateSeries(int mediaId, Series series, long userId);
    Series getSeriesById(int mediaId);
}
