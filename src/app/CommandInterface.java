package app;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import classes.TDCategory;
import classes.TDEntry;

//TODO para una version futura cambiar el modelado de los parametros
public class CommandInterface {
	private static Facade facade = new Facade();

	public static void main(String[] args) throws InterruptedException {
		// Al crear date se le asigna el tiempo actual auntomaticamente
		Map<String, Runnable> commands = new HashMap<>();
		commands.put("addE", () -> addEntry(args));
		commands.put("addC", () -> addCategory(args));
		commands.put("delE", () -> delEntry(args));
		commands.put("delC", () -> delCategory(args));
		commands.put("check", () -> checkEntry(args));
		commands.put("uncheck", () -> uncheckEntry(args));
		commands.put("todos", () -> displayEntries(args));
		commands.put("categories", () -> displayCategories());
		commands.put("help", () -> displayHelp());
		try {
			if (commands.containsKey(args[0])) {
				commands.get(args[0]).run();
				return;
			} else {
				System.out.println(args[0] + " no se reconoce como un comando del programa gotodo.");
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("No se incluyeron argumentos.");
		}
		return;
	}

	private static void displayCategories() {
		List<TDCategory> categories = facade.getCategories();
		if (categories == null) {
			System.out.println("No se han creado categorias");
			return;
		}
		System.out.println("      Numero\t\tNombre\t    Pendientes\t\tCreado");
		int i = 0;
		for (TDCategory category : categories) {
			String catName = formatName(category.getName());
			System.out.println(
				"\t" + (i++) +	"\t" + catName + "\t\t" + category.getNPending() + "\t\t" + category.getDCreated().toString());
		}
	}

	private static void displayEntries(String[] args) {
		try{
			List<TDEntry> toDisplay = new ArrayList<TDEntry>();
			char displayFlag = getDisplayFlag(args);
			Boolean flag = null;
			if(displayFlag != 'a')
				flag = displayFlag == 'd' ? true : false;			
			toDisplay = facade.getEntries(flag);
			// TODO rellenar con el resto de parametros
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			System.out.println("      Numero\t Titulo\t\t  Categoria\t Creado \t Checkeado\t  Contenido");
			String cr, ch, xString;
			int i = 0;
			for (TDEntry entry : toDisplay) {
				cr = format.format(entry.getDCreated());
				ch = entry.isChecked() ? format.format(entry.getDChecked()) : "--/--/----";
				xString = entry.isChecked() ? "X" : " ";
				System.out.println(
					"[" + xString + "]\t" + i + ".\t" + formatName(entry.getTitle()) + "\t" + formatName(entry.getCategory()) + "\t" 
					+ cr +"\t" + ch + " |\t" + entry.getBody());
					i++;
				}
		} catch (Exception e) {
			System.out.println("No se pudieron mostrar las entradas. Error " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void displayHelp(){
		System.out.println(
			"GoTODO es un programa para mantener organizadas las tareas por hacer en una computadora.\n\n" +
			"  gotodo {comando} <parametros> \n\n" +
			"\t addC \t\t | Annade una categoria \t||  {NbrCat} \n" + 
			"\t addE \t\t | Annade una entrada \t\t||  [-cno 1 | -c NbrCat] [-t TituloEntrada] -b {Cuerpo de la entrada} \n" +
			"\t delE \t\t | Borra una entrada \t\t||  { -u 1 | { -c NbrCat | -cno 1 } -t TEntrada } \n" +
			"\t delC \t\t | Borra una categoria \t\t||  {-c NbrCat | -cno 1} \n" +
			"\t check \t\t | Marca una categoria \t\t||  { -u 1 | { -c NbrCat | -cno 1 } -t TEntrada } \n" +
			"\t uncheck \t | Desmarca una categoria \t||  { -cno 1 | -c NbrCat} -t {TEntrada} \n" +
			"\t todos \t\t | Muestra una lista de tareas \t||  [ -c | -cno ] [ -sd | -sdc ] [-a|-p|-d] \n" +
			"\t categories \t | Muestra las categorias"
		);
	}

	private static void uncheckEntry(String[] args) {
		try{
			TDCategory cat = getCategory(args);
			String title = getTitle(args);
			int result = 0;
			if(cat != null && !title.equals("")){
				result = facade.setCheckEntry(cat.getName(), title, false);
			}
			if(result == 1){
				System.out.println("La entrada se ha desmarcado con exito.");
			} else {
				throw new Exception("code0");
			}

		} catch (Exception e) {
			System.out.println("No se ha podido desmarcar la entrada. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void checkEntry(String[] args) {
		try {
			TDCategory cat = getCategory(args);
			String title = getTitle(args);
			Integer numU = getUncheckedIndex(args);
			int result = 0;
			if(numU != -1){
				List<TDEntry> entries = facade.getEntries(false);
				if(numU >= 0 & numU <= entries.size()){
					result = facade.checkEntry(numU);
				}
			} else if (cat != null & !title.equals("")){
				result = facade.setCheckEntry(cat.getName(), title, true);
			} else {
				System.out.println("No se ha proveido alguno de los parametros necesarios.");
				throw new Exception("paramError");
			}
			if(result == 1){
				System.out.println("Se ha checkeado la entrada.");
			} else {
				throw new Exception("code0");
			}
		} catch (Exception e) {
			System.out.println("No se ha podido checkear la entrada. Error " + e.getMessage());
		}
	}
	
	private static void delCategory(String[] args) {
		try {
			int result = 0;
			TDCategory cat = getCategory(args);
			if(cat != null){
				result = facade.deleteCategory(cat.getName());
			} else {
				System.out.println("No se ha encontrado la categoria indicada.");
				throw new Exception("nonExistingCategory");
			}
			if (result == 1){
				System.out.println("Se ha borrado la categoria exitosamente.");
			} else {
				throw new Exception("code0");
			}
		} catch (Exception e) {
			System.out.println("No se ha podido borrar la categoria indicada. Error " + e.getMessage());;
		}
		
	}
	
	private static void delEntry(String[] args) {
		try{
			TDCategory cat = getCategory(args);
			int numU = getUncheckedIndex(args);
			String title = getTitle(args);
			int result = 0;
			if(numU != -1 & cat == null & title.equals("")){
				result = facade.delEntry(numU);
			} else if (cat != null & !title.equals("") & numU == -1){
				result = facade.delEntry(cat.getName(), title);
			} else {
				System.out.println("No se han ingresado los parametros correctamente.");
				throw new Exception("paramError");
			}
			if (result == 1){
				System.out.println("Se ha eliminado la entrada correctamente.");
			} else {
				throw new Exception("code0");
			}
		} catch (Exception e) {
			System.out.println("No se ha podido eliminar la entrada. Error: " + e.getMessage());
		}
	}
	
	private static void addCategory(String[] args) {
		try {
			int result = 0;
			if (args.length > 1) {
				String res = "";
				for (int i = 1; i < args.length; i++) {
					res += args[i];
				}
				res.trim();
				if (res.isEmpty()) {
					System.out.println("No se ha incluido un nombre para la categoria.");
				} else if (res.contains("-")) {
					System.out.println("No se permiten guiones en el titulo.");
				} else {
					result = facade.addCategory(res);
				}
				if(result == 1){
					System.out.println("Se ha annadido la categoria exitosamente.");
				} else {
					throw new Exception("code0");
				}
			} else {
				System.out.println("No se ha incluido un nombre para la categoria.");
			}
		} catch (IOException e) {
			System.out.println("Algo ha sucedido con el manejo de archivos. Estos son los detalles del error");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("No se ha podido annadir la categoria. Error " + e.getMessage());
		}
	}
	
	private static void addEntry(String[] args) {
		try {
			TDCategory cat = getCategory(args);
			String title = getTitle(args);
			String body = getBody(args);
			if(body.equals("")){
				System.out.println("El cuerpo es obligatorio para annadir la entrada.");
				throw (new Exception("paramError"));
			}
			int result;
			if(cat == null){
				if(title.equals("")){
					result = facade.addEntry(body);
				} else {
					result = facade.addEntry(title, body, true);
				}
			} else {
				if(title.equals("")){
					result = facade.addEntry(cat.getName(), body, false);
				} else {
					result = facade.addEntry(cat.getName(), body, title);
				}
			}
			if(result == 1)
			System.out.println("Se ha ingresado la entrada exitosamente.");
			else throw new Exception("Code0");
		} catch (Exception e) {
			System.out.println("No se ha podido annadir la entrada. Error: " + e.getMessage() );
		}
	}
	
	//Estos son metodos auxiliares
	
	private static String formatName(String name) {
		return name.length() > 15 ? name.substring(0, 12) + "..."
		: (name.length() > 7 ? name 
		: name + "\t");
	}
	
	private static TDCategory getCategory(String[] args) throws Exception {
		TDCategory category = null;
		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			if(arg.equals("-cno")){
				try{
					int index = Integer.parseInt(args[i+1]);
					category = facade.getCategories().get(index);
				} catch (NumberFormatException e){
					System.out.println("El parametro -cno solo puede ser un numero");
					throw (new Exception("paramError"));
				} catch (IndexOutOfBoundsException e) {
					System.out.println("El parametro -cno solo puede ser un numero");
					throw (new Exception("catIndexError"));
				} break;
			}
			if(arg.equals("-c")){
				int w = 1;
				String name = "";
				while(i+w<args.length && !args[i + w].startsWith("-")){
					name += args[i+w] + " ";
					w++;
				}
				name = name.trim();
				if(name == ""){
					System.out.println("No se ha incluido un nombre de categoria.");
					throw (new Exception("paramError"));
				}
				category = facade.getCategory(name);
				if(category == null){
					System.out.println("No existe la categoria con el nombre ingresado.");
					throw( new java.lang.Exception("catNameError"));
				} break;
			}
		}
		return category;
	}
	
	private static String getTitle(String[] args) throws Exception {
		String title = "";
		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			if(arg.equals("-t")){
				int w = 1;
				while(i+w<args.length && !args[i + w].startsWith("-")){
					title += args[i+w] + " ";
					w++;
				}
				title = title.trim();
				if(title.equals("")){
					System.out.println("No se ha incluido un titulo para la entrada.");
					throw (new Exception("paramError"));
				}
				break;
			}
		}
		return title;
	}
	
	private static String getBody(String[] args) throws Exception {
		String body = "";
		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			if(arg.equals("-b")){
				int w = 1;
				while(i+w<args.length && !args[i + w].startsWith("-")){
					body += args[i+w] + " ";
					w++;
				}
				body = body.trim();
				break;
			}
		}
		if(body.equals("")){
			System.out.println("No se ha incluido el cuerpo de la entrada.");
			throw (new Exception("paramError"));
		}
		return body;
	}
	
	private static Integer getUncheckedIndex(String[] args) throws Exception {
		Integer uIndex = -1; 
		try{
			for (int i = 1; i < args.length; i++) {
				String arg = args[i];
				if(arg.equals("-u")){
					uIndex = Integer.parseInt(args[i+1]);	
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("El parametro -u ingresado no es valido.");
			throw new Exception("paramError");
		} catch (ArrayIndexOutOfBoundsException e){
			System.out.println("No se ha ingresado un parametros -u");
			throw new Exception("paramError");
		}
		return uIndex;
	}
	
	private static char getDisplayFlag(String[] args) throws Exception {
		boolean found = false;
		char flag = 'p';
		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			if(arg.equals("-p") | arg.equals("-d") | arg.equals("-a")){
				if(!found){
					found = true;
					flag = arg.charAt(1);
				} else {
					System.out.println("Se ha dado mas de una bandera de estado. Solo se puede ingresar una.");
					throw new Exception("paramError");
				}
			}
		}
		return flag;
	}

}
