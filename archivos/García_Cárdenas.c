//Angel Alberto Garc�a Cardenas
#include <stdio.h>
#include <stdlib.h>

//Angel Alberto Garc�a Cardenas

int main(){
    int aleatorio,n, p;
    
    srand(time(NULL));
    printf("Indique la cantidad de numeros aleatorios a generar, por favor: ");
    scanf("%d", &n);

    int i=0;   
    
    for (i=0; i<n; i++){
     //Entre 12 y 92       
        aleatorio=rand()%(92-12+1)+12;
        
         p=aleatorio%2;
          if(p==0){
            printf("%d \t",aleatorio);
              
          }else{
              i--;
          }
          
    }
return 0;
}
 FIA.txt;García_Cárdenas.c