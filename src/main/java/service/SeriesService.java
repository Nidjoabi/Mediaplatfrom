package service;


import Modules.Game;
import Modules.Series;
import persistence.ISeriesRepository;


public class SeriesService implements ISeriesService {
    private static SeriesService instance = null;
    private final ISeriesRepository seriesRepository;

    public SeriesService(ISeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    public static SeriesService getInstance(ISeriesRepository seriesRepository){
        if (instance == null) {
            instance = new SeriesService(seriesRepository);
        }
        return instance;
    }
    @Override
    public Series addSeries(Series series, long userId) {

        return seriesRepository.addSeries(series, userId);
    }

    @Override
    public boolean deleteSeries( int mediaId, long userId) {
        if (mediaId <= 0) throw new IllegalArgumentException("mediaId is missing");
        return seriesRepository.deleteSeries(mediaId, userId);
    }

    @Override
    public Series updateSeries(int mediaId, Series series, long userId) {
        if (series == null) throw new IllegalArgumentException("series is null");
        return seriesRepository.updateSeries(mediaId, series, userId);
    }

    @Override
    public Series getSeriesById(int mediaId) {
        if (mediaId <= 0) throw new IllegalArgumentException("mediaId is missing");
        return seriesRepository.getSeriesById(mediaId);
    }
}
