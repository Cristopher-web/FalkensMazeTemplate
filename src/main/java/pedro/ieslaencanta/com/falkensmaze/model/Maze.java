/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pedro.ieslaencanta.com.falkensmaze.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import pedro.ieslaencanta.com.falkensmaze.Size;

/**
 *
 * @author Pedro
 */
@XmlRootElement
public class Maze implements Serializable{

    private Size size;
    private Block[][] blocks;
    private double time;
    private String sound;

    public Maze() {
    }

    public void init() {
        this.setBlocks(new Block[this.getSize().getHeight()][this.getSize().getWidth()]);
        for (int i = 0; i < this.getBlocks().length; i++) {
            for (int j = 0; j < this.getBlocks()[i].length; j++) {
                this.getBlocks()[i][j] = new Block();

            }
        }
    }

    public void reset() {
        for (int i = 0; i < this.getBlocks().length; i++) {
            for (int j = 0; j < this.getBlocks()[i].length; j++) {
                this.getBlocks()[i][j] = null;

            }
        }
        this.setBlocks(null);
    }

    public void reset(Size newsize) {
        this.reset();;
        this.setSize(newsize);
        this.init();
    }

    public void setBlockValue(String value, int row, int col) {
        this.getBlocks()[col][row].setValue(value);
    }

    public String getBlockValue(int row, int col) {
        return this.getBlocks()[row][col].getValue();
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public Block[][] getBlocks() {
        return blocks;
    }

    public void setBlocks(Block[][] blocks) {
        this.blocks = blocks;
    }
    //Todos los load se pone la extension para que solo acepte ese formato, si no lanza una excepcion
    public static Maze load(File file) throws FileNotFoundException, IOException, ClassNotFoundException, JAXBException, Exception  {
        String extension = file.getName().substring(file.getName().lastIndexOf(".")+ 1);
        if(extension.equals("bin")){
            return loadBin(file);
        }else if(extension.equals("xml")){
            return loadXML(file);
        }else if(extension.equals("json")){
            return loadJSON(file);
        }else{
             throw new Exception("Tipo no permitido");
        }
    }
    //Todos los save igual que en el load pero en este si la musica no esta puesta este tambien da excepcion
    public static void save(Maze maze, File file) throws Exception {
        String extension = file.getName().substring(file.getName().lastIndexOf(".")+ 1);
        if (maze.sound == null || maze.sound.equals("")) {
            throw new Exception("Es necesario indicar el sonido del laberinto");
        }
        if(extension.equals("bin")){
            saveBin(maze, file);
        }else if(extension.equals("xml")){
            saveXML(maze, file);
        }else if(extension.equals("json")){
            saveJSON(maze, file);
        }else{
            throw new Exception("Tipo no permitido");
        }
    }
    //load de JSON este lee el fichero con FileReader se le pasa el file y hacemo un maze con gson.fromJson para
    //que pueda leer el formato json aparte de que lo pueda poner en el formato que tenemos en nuestra clase.
    private static Maze loadJSON(File file) throws FileNotFoundException {
    Gson gson = new Gson();
    FileReader reader = new FileReader(file);
    Maze maze = gson.fromJson(reader, Maze.class);
    return maze;
    }
    //load de XML este se hace con JAXBContext con este sacamos una instacia de la clase para poder leer el fichero xml
    //y poder ponerlo en formato de clase maze despues con el unmarshaller leemos el fichero y devolvemos el resultado
    private static Maze loadXML(File file) throws JAXBException  {
        JAXBContext context = JAXBContext.newInstance(Maze.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Maze maze = (Maze) unmarshaller.unmarshal(file);
        return maze;  
    }
    //load de bin con este leemos el archivo de binario con FileInputStream y ObjecInputStream creamos una instacia de maza y leemos el
    //objeto de binario para poder pasarlo a la clase.
    public static Maze loadBin(File file) throws FileNotFoundException, IOException, ClassNotFoundException  {
      FileInputStream os = new FileInputStream(file);
      ObjectInputStream oos = new ObjectInputStream(os);
      Maze m = (Maze) oos.readObject();
      return m;
    }
    //saveJson en este metodo guarda el maze para poder pasarlo a un fichero en formato json
    //esto se hace a partir del Gson con gson.toJson para darle el formato json y despues guardarlo
    //en un fichero
    private static void saveJSON(Maze maze, File file) throws IOException  {
      String jsonString;
      GsonBuilder gbu = new GsonBuilder();
      Gson gson = gbu.create();
      jsonString = gson.toJson(maze);
      FileWriter fw = new FileWriter(file);
      fw.write(jsonString);
      fw.close();
    }
    //saveXML se hace con JAXBContext y es basicamente casi igual que el load pero este guarda la clase maze
    //con el marshaller.setProperty le damos el formato xml y con el marshaller.marshal guardamos el maze en el archivo
    private static void saveXML(Maze maze, File file) throws JAXBException  {
      JAXBContext context = JAXBContext.newInstance(maze.getClass());
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(maze, file);
    }
    //saveBin se hace con fileoutputstream este guarda la maze en binario, con el objextooutputstream ya que maze es un objeto
    //escribimos en el fichero el maze oos.writeObject despues cerramos el fileoutputstream y el objectoutputstream
    public static void saveBin(Maze maze, File file) throws IOException  {
      FileOutputStream os = new FileOutputStream(file);
      ObjectOutputStream oos = new ObjectOutputStream(os);
      oos.writeObject(maze);
      oos.close();
      os.close();
    }
}
