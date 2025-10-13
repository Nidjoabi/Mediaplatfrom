package service;


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
    public Series addSeries(Series series) {
        if (series == null) {
            System.out.println("Series is null");
        }
        return seriesRepository.addSeries(series);
    }
}
