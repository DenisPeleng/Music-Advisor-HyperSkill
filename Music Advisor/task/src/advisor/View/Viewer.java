package advisor.View;

import java.util.List;

public class Viewer {

    public static boolean printInfoInPages(int currentPage, int itemsPerPage, List<String> infoArr) {
        int maxAmountPages = infoArr.size() % itemsPerPage == 0 ? infoArr.size() / itemsPerPage : (infoArr.size() / itemsPerPage) + 1;
        if ((currentPage > maxAmountPages) || (currentPage <= 0)) {
            System.out.println("No more pages.");
            return false;
        }
        for (int i = (currentPage - 1) * itemsPerPage; i < currentPage * itemsPerPage; i++) {
            System.out.println(infoArr.get(i));
        }
        System.out.printf("---PAGE %d OF %d---\n", currentPage, maxAmountPages);
        return true;
    }

}




