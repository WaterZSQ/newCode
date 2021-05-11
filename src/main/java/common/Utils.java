package common;

public class Utils {

    public static void printBooleanArr(boolean[][] arr){
        if(arr == null || arr.length == 0){
            return ;
        }
        for(int i = 0;i < arr.length;i++){
            for(int j = 0;j < arr[0].length;j++){
                if(arr[i][j]){
                    System.out.print("1 ");
                }else{
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
