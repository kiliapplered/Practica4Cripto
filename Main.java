/* 
 * @Autor Najera Noyola Karla Andrea
 * @Fecha 4 de noviembre de 2022
 * @Descripción Práctica que implementa el algoritmo MD2
*/

// Bibliotecas utilizadas.
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.List;

/*
 * Clase principal del programa a partir de la cual se implementa el algoritmo. 
 */
public class Main {
    public static void main(String[] args) {

        // Definición de variables a utilizar
        Scanner sc= new Scanner(System.in); // Leer entradas del teclado
        // Variables asociadas al algoritmo de MD2
        int tam_bloque=16; // MD2 trabaja con un bloque fijo de 16 bits. 
        int sBox[]= { // SBox que actua como tabla de sustitución y que incluye decimales de PI.  
                    41, 46, 67, 201, 162, 216, 124, 1, 61, 54, 84, 161, 236, 240, 6, 19,
                    98, 167, 5, 243, 192, 199, 115, 140, 152, 147, 43, 217, 188, 76, 130, 202,
                    30, 155, 87, 60, 253, 212, 224, 22, 103, 66, 111, 24, 138, 23, 229, 18,
                    190, 78, 196, 214, 218, 158, 222, 73, 160, 251, 245, 142, 187, 47, 238, 122,
                    169, 104, 121, 145, 21, 178, 7, 63, 148, 194, 16, 137, 11, 34, 95, 33,
                    128, 127, 93, 154, 90, 144, 50, 39, 53, 62, 204, 231, 191, 247, 151, 3,
                    255, 25, 48, 179, 72, 165, 181, 209, 215, 94, 146, 42, 172, 86, 170, 198,
                    79, 184, 56, 210, 150, 164, 125, 182, 118, 252, 107, 226, 156, 116, 4, 241,
                    69, 157, 112, 89, 100, 113, 135, 32, 134, 91, 207, 101, 230, 45, 168, 2,
                    27, 96, 37, 173, 174, 176, 185, 246, 28, 70, 97, 105, 52, 64, 126, 15,
                    85, 71, 163, 35, 221, 81, 175, 58, 195, 92, 249, 206, 186, 197, 234, 38,
                    44, 83, 13, 110, 133, 40, 132, 9, 211, 223, 205, 244, 65, 129, 77, 82,
                    106, 220, 55, 200, 108, 193, 171, 250, 36, 225, 123, 8, 12, 189, 177, 74,
                    120, 136, 149, 139, 227, 99, 232, 109, 233, 203, 213, 254, 59, 0, 29, 57,
                    242, 239, 183, 14, 102, 88, 208, 228, 166, 119, 114, 248, 235, 117, 75, 10,
                    49, 68, 80, 180, 143, 237, 31, 26, 219, 153, 141, 51, 159, 17, 131, 20};

        // Datos de bienvenida del programa.
        System.out.println(" ************        MD2        ************ ");
        System.out.println(" *****    Created by: Karla Najera     ***** ");

        // Solicitud del mensaje a codificar. 
        System.out.print("\n\nMessage: ");
        String mensajeCadena=sc.nextLine();

        // Se convierte la cadena en lista de enteros con la representación en ASCII.
        int longMensaje=mensajeCadena.length();
        List<Integer> mensaje= mensajeCadena.chars().boxed().collect(Collectors.toList());


        // Una vez que se tiene el mensaje a cifrar, se da INICIO AL ALGORITMO
        
        // *** Paso 1 - Padding: Se incrementa el mensaje de tal forma que sea múltiplo de 16 bytes
        int padding= tam_bloque-(longMensaje%tam_bloque);
        for(int i=0; i<padding; i++){ // Se agregan "i" bytes de valor "i"
            mensaje.add(padding); 
        }

        // *** Paso 2 - Checksum: Se agrega una suma de comprobación de 16 bytes al mensaje obtenido 
        //                        en el paso anterior.  
        longMensaje+=padding;
        int bytes=0;
        int checkbyte_previo=0; // Se usa como vector de inicialización.
        int checksum[] = new int[tam_bloque]; // Se usa un arreglo ya que inicialmente se llena con 0's. 
        // Se procesa cada bloque de 16 palabras (16 bytes por bloque).
        for(int i=0; i<longMensaje/tam_bloque; i++){
            for(int j=0; j<tam_bloque; j++){
                bytes=mensaje.get(i*tam_bloque+j);
                checksum[j] ^= sBox[bytes^checkbyte_previo]; //Operación XOR. Se considera una fe de erratas del RFC 1319. 
                checkbyte_previo=checksum[j];
            }
        }

        // Se añade uno a uno los carácteres generados al mensaje procesado. 
        for(int i=0; i<tam_bloque; i++){
            mensaje.add(checksum[i]);
        }

        // *** Paso 3 - Hash: Se realiza el hash
        longMensaje=mensaje.size(); 
        int rondas=18, tam_buffer=48;

        // Se crea un arreglo de 48 posiciones lleno con 0's
        int resumen[]= new int[48]; // Java por defecto llena los arreglos creados con 0's en sus posiciones

        // Se genera el digest (resumen) del mensaje. 
        for(int i=0; i<longMensaje/tam_bloque; i++){
            // Se copia el bloque i en la sección central de la matriz. 
            // La última sección de la matriz se llena con la sección frontal XOR sección central.
            for(int j=0; j<tam_bloque; j++){
                resumen[tam_bloque+j] =  mensaje.get(i*tam_bloque+j);
                resumen[tam_bloque*2+j] = resumen[tam_bloque+j]^resumen[j];
            }
            int check=0;
            // Se realizan las rondas de encriptación sobre el arreglo completo. 
            // se hace XOR entre byte actuak con el byte anterior (sustituido). 
            for(int j=0; j<rondas; j++){
                for(int k=0; k<tam_buffer; k++){
                    check=resumen[k]^sBox[check];
                    resumen[k]=check;
                }
                check=(check+j)%256;
            }
        }

        // Fin del algoritmo. 

        // De lo obtenido, se imprimen los primeros 16 bytes. Para ello se colocan en una nueva variable
        // y se mandan a imprimir a pantalla
        String mensajeFinal="";
        String hex="";
        for(int i=0; i<16; i++){
            hex=Integer.toHexString(resumen[i]);
            if(hex.length()==1){
                hex="0"+hex;
            }
            mensajeFinal+=hex;
        }

        // Se imprime el mensaje en la pantalla
        System.out.println("Hash: "+mensajeFinal);

        // Finaliza la ejecución del programa. 
        sc.close();
    }
}