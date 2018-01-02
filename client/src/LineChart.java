import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LineChart {
    public static CategoryChart getChart(AdminViewController controller) {
        String[] days = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};

        List<String> xData = new ArrayList<>();
        List<Integer> yData = new ArrayList<>();

        xData.addAll(Arrays.asList(days));

        for(int i = 1; i < 8; i++) {
            try {
                yData.add(controller.getGraphInfo(Integer.toString(i)));
            } catch (Exception e) {}
        }

        CategoryChart chart = new CategoryChartBuilder()
                .width(600).height(500)
                .title("Статистика вылетов по дням")
                .yAxisTitle("Количество вылетов").build();

        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setPlotGridLinesVisible(false);
        chart.addSeries("some name u will never see", xData, yData);

        return chart;
    }
}
