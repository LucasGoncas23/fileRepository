import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringJoiner;

public class ServerTCP {

	public static void main(String[] args) {
		int number = 0;

		int serverPort = 7000;
		try (ServerSocket listenSocket = new ServerSocket(serverPort)) {
			System.out.println("Listening at port 7000");
			System.out.println("LISTEN SOCKET=" + listenSocket);
			while (true) {
				Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
				System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
				number++;
				new Connection(clientSocket, number);
			}
		} catch (IOException e) {
			System.out.println("Listen:" + e.getMessage());
		}
	}
}

//= Thread to handle each communication channel with a client
class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	int thread_number;
	ArrayList<User> users = new ArrayList<>();

	public Connection(Socket aClientSocket, int number) {
		thread_number = number;
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			this.start();
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}

	// =============================
	public void run() {

		// ArrayList<User> users = new ArrayList<User>();
		BufferedReader objReader = null;
		try {
			//READ users.txt FILE and WRITE IN ARRAYLIST
			String strCurrentLine;
			String[] arrStr;
			objReader = new BufferedReader(new FileReader(
					System.getProperty("user.dir")+"\\Server\\users.txt"));

			while ((strCurrentLine = objReader.readLine()) != null) {
				arrStr = strCurrentLine.split(",", -1);
				User user = new User(arrStr[0], arrStr[1], arrStr[2], arrStr[3], arrStr[4], arrStr[5]);
				users.add(user);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objReader != null)
					objReader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		//String response;
		String username;
		String password;
		String phone;
		String address;
		String expiration;
		String college;
		String oldPass;
		String newPass;
		String dirCurrentClient;
		String dirCurrentServer;
		//String homeClient;
		//String homeServer;
		try {
			while (true) {
				// an echo server
				String option = in.readUTF();
				if (option.equals("1")) {  //1- REGISTER USER
					username = in.readUTF();
					password = in.readUTF();
					phone = in.readUTF();
					address = in.readUTF();
					expiration = in.readUTF();
					college = in.readUTF();
					//VERIFY NEW USER
					if (RegisterUser(username, password, phone, address, expiration, college)) {
						//System.out.println("Thread/client[" + thread_number + "]");
						System.out.println("User " + username + " successfully registered");
						//NEW USER REGISTERED AND AUTHENTICATED -----------------> CLIENT ENTERS MENU
						out.writeBoolean(true);
						//out.writeUTF(username);
						try {
							//CREATES USER DIR
							StringJoiner dir = new StringJoiner("\\");   //StringeJoiner object
							dir.add(System.getProperty("user.dir")+"\\Server\\Clients");
							//username = Normalizer.normalize(username, Normalizer.Form.NFD)/*.replaceAll("[^\\p{ASCII}]", "")*/;
							dir.add(username);
							Path path = Paths.get(dir.toString());

							Files.createDirectories(path);

							System.out.println("Dir created!");

							StringJoiner dirSec = new StringJoiner("\\");   //StringeJoiner object
							dirSec.add(System.getProperty("user.dir")+"\\Server_sec\\Clients");
							//username = Normalizer.normalize(username, Normalizer.Form.NFD)/*.replaceAll("[^\\p{ASCII}]", "")*/;
							dirSec.add(username);
							Path pathSec = Paths.get(dirSec.toString());

							Files.createDirectories(pathSec);

						} catch (IOException e) {

							System.err.println("Failed to create directory!" + e.getMessage());

						}
						//CREATE FILE WITH USER INFO
						try {
							FileWriter myWriter = new FileWriter(System.getProperty("user.dir")+"\\Server\\Clients\\"+username+"\\"+username+".txt");

							String str = "Name: " + username + "\n" +
									"Password: " + password + "\n" +
									"Phone: " + phone + "\n" +
									"Address: " + address + "\n" +
									"Expiration: " + expiration + "\n" +
									"College: " + college + "\n";
							myWriter.write(str);
							myWriter.close();
							System.out.println("File created!");

							FileWriter myWriterSec = new FileWriter(System.getProperty("user.dir")+"\\Server_sec\\Clients\\"+username+"\\"+username+".txt");
							myWriterSec.write(str);
							myWriterSec.close();

						} catch (IOException e) {
							System.out.println("An error occurred.");
							e.printStackTrace();
						}
					} else {
						System.out.println("User " + username + " already exists");
						out.writeBoolean(false);
					}

				} else if (option.equals("2")) {	//LOGIN
					username = in.readUTF();
					password = in.readUTF();
					//System.out.println("Thread/client[" + thread_number + "]: " + username);
					//CHECK IF THE USER AND PASSWORD EXIST AND ARE CORRECT
					if (CheckUser(username, password)) {
						//USER AUTHENTICATED -----------------> CLIENT ENTERS MENU
						System.out.println("User " + username + " authenticated");
						out.writeBoolean(true);
						//CREATE DIRECTORY AND FILE WITH USER INFO IF IT DOES NOT EXIST YET
						File fileCheck = new File(System.getProperty("user.dir")+"\\Server\\Clients\\"+username);
						//File fileCheckSec = new File(System.getProperty("user.dir")+"\\Server_sec\\Clients\\"+username);
						if(!fileCheck.exists()) {
							//create dir
							try {
								//CREATE USER DIR ON THE SERVER
								StringJoiner dir = new StringJoiner("\\");   //StringeJoiner object
								dir.add(System.getProperty("user.dir")+"\\Server\\Clients");
								dir.add(username);
								Path path = Paths.get(dir.toString());

								Files.createDirectories(path);

								StringJoiner dirSec = new StringJoiner("\\");   //StringeJoiner object
								dirSec.add(System.getProperty("user.dir")+"\\Server_sec\\Clients");
								dirSec.add(username);
								Path pathSec = Paths.get(dirSec.toString());

								Files.createDirectories(pathSec);

								//System.out.println("Directory is created!");

							} catch (IOException e) {

								System.err.println("Failed to create directory!" + e.getMessage());

							}
							//CREATE USER FILE ON SERVER
							try {
								FileWriter myWriter = new FileWriter(System.getProperty("user.dir")+"\\Server\\Clients\\"+username+"\\"+username+".txt");
								FileWriter myWriterSec = new FileWriter(System.getProperty("user.dir")+"\\Server_sec\\Clients\\"+username+"\\"+username+".txt");
								User loggedUser = FindUser(username,password);
								myWriter.write("Nome: "+username + "\n" +
										"Password: "+ password + "\n"+
										"Phone: "+loggedUser.phone + "\n" +
										"Address: "+loggedUser.address + "\n" +
										"Expiration: "+loggedUser.expiration + "\n"+
										"College: "+loggedUser.college + "\n");
								myWriter.close();

								myWriterSec.write("Nome: "+username + "\n" +
										"Password: "+ password + "\n"+
										"Phone: "+loggedUser.phone + "\n" +
										"Address: "+loggedUser.address + "\n" +
										"Expiration: "+loggedUser.expiration + "\n"+
										"College: "+loggedUser.college + "\n");
								myWriterSec.close();

							} catch (IOException e) {
								System.out.println("An error occurred.");
								e.printStackTrace();
							}
							System.out.println("FIRST user "+username+" authentication. Directory and file created.");
						}else {
							System.out.println("User "+username+" have already authenticated in the past");
						}

					} else {
						System.out.println("Wrong username and/or password");
						out.writeBoolean(false);
					}

				}else if (option.equals("3")) { //CLIENT WANTS TO LEAVE
					break;
				}
				else if(option.equals("Change password")) {
					username = in.readUTF();
					password = in.readUTF();
					oldPass = in.readUTF();
					newPass = in.readUTF();
					System.out.println("User '"+username +"' wants to change password");
					//changes (or not) the pass and returns true (or false) to the client
					boolean changeP = changePassword(username, password, oldPass, newPass);
					out.writeBoolean(changeP);

					if(changeP) {
						//update password in user's personal file
						try {
							FileWriter myWriter = new FileWriter(System.getProperty("user.dir")+"\\Server\\Clients\\"+username+"\\"+username+".txt");
							//FileWriter myWriterSec = new FileWriter(System.getProperty("user.dir")+"\\Server_sec\\Clients\\"+username+"\\"+username+".txt");
							User loggedUser = FindUser(username,newPass);
							myWriter.write("Nome: "+username + "\n" +
									"Password: "+ newPass + "\n"+
									"Phone: "+loggedUser.phone + "\n" +
									"Address: "+loggedUser.address + "\n" +
									"Expiration: "+loggedUser.expiration + "\n"+
									"College: "+loggedUser.college + "\n");
							myWriter.close();

						} catch (IOException e) {
							System.out.println("An error occurred.");
							e.printStackTrace();
						}
					}


				}
				else if(option.equals("List client files")) {
					System.out.println("Listing client local files...");
					username = in.readUTF();
					password = in.readUTF();
					String currentDir = in.readUTF();
					File file = new File(currentDir);

					// returns an array of all files
					String[] fileList = file.list();
					int len = fileList.length;
					out.writeInt(len);
					for(String str : fileList) {
						out.writeUTF(str);
						System.out.println(str);
					}

				}
				else if(option.equals("List server files")) {
					System.out.println("Listing client files on the server...");
					username = in.readUTF();
					password = in.readUTF();
					String currentDir = in.readUTF();
					File file = new File(currentDir);

					// returns an array of all files
					String[] fileList = file.list();
					int len = fileList.length;
					out.writeInt(len);
					for(String str : fileList) {
						out.writeUTF(str);
						System.out.println(str);
					}

				}
				else if(option.equals("Download a file from the server")) {
					username = in.readUTF();
					String currentDir = in.readUTF();
					String file = in.readUTF();

					//current dir + file
					File fileCheck = new File(currentDir+"\\"+file);
					File fileCheck2=new File(System.getProperty("user.dir")+"\\"+username+"Files\\"+file);
					if(fileCheck.exists() && (!fileCheck2.exists())) {
						copyFile(fileCheck.toPath(),fileCheck2.toPath());
						System.out.println("File downloaded from the server");
					}else if(fileCheck.exists() && fileCheck2.exists()) {
						System.out.println("File with name '"+ file+ "' already exists in the user's '"+username+"' local storage!");
						//substitute or cancel?
					}
					else {
						System.out.println("File '"+file+ "' does not exist on the server!");
					}

					/*ServerSocket newSocket = new ServerSocket(newPort);
					NewThread newThread = new NewThread(newSocket, file, "download",username);
					int newPort = newSocket.getLocalPort();
					out.writeInt(newPort);*/
				}
				else if(option.equals("Upload a file to the server")) {
					username = in.readUTF();
					String currentDir = in.readUTF();
					String file = in.readUTF();

					//current dir + file
					File fileCheck = new File(currentDir+"\\"+file);
					File fileCheck2 = new File(System.getProperty("user.dir")+"\\Server\\Clients\\"+username+"\\"+file);
					File fileCheckSec = new File(System.getProperty("user.dir")+"\\Server_sec\\Clients\\"+username+"\\"+file);
					if(fileCheck.exists() && (!fileCheck2.exists())) {
						copyFile(fileCheck.toPath(),fileCheck2.toPath());
						System.out.println("File uploaded to the server");
						if(!fileCheckSec.exists()) {
							copyFile(fileCheck.toPath(),fileCheckSec.toPath());
							System.out.println("File uploaded to secondary server");
						}
						
					}else if(fileCheck.exists() && fileCheck2.exists()) {
						System.out.println("File with name '"+ file+ "' already exists on the server!");
						//substitute or cancel?
					}
					else {
						System.out.println("File with name '"+ file+ "' does not exist in the user's '"+username+"' local storage!");
					}

				}
				else if(option.equals("Change client current dir")) { //5
					username = in.readUTF();
					dirCurrentClient = in.readUTF();
					String cmd = in.readUTF();
					String[] cmdSplit = cmd.split(" ");
					if(cmd.equals("cd")) {
						out.writeUTF(dirCurrentClient);
					}
					else {
						if(cmdSplit[0].equals("cd")) {
							if(cmdSplit[1] != null) {
								if(cmdSplit[1].equals("..") || cmdSplit[1].equals("home")) {
									//return to home dir
									out.writeUTF(System.getProperty("user.dir")+"\\" + username +"Files");
								}
								else {
									for(int i = 1; i < cmdSplit.length ; i++) {
										File checkDir = new File(dirCurrentClient + "\\" + cmdSplit[i]);
										if(checkDir.exists()) {
											dirCurrentClient = checkDir.getPath(); //This will be used to then list the files only in this directory
											out.writeUTF(checkDir.getPath());
										}
										else {
											out.writeUTF(dirCurrentClient);
											System.out.println("Dir does not exists");
										}
									}
								}

							}else {
								out.writeUTF(dirCurrentClient);
								System.out.println(dirCurrentClient);
							}

						}
						else {
							out.writeUTF(dirCurrentClient);
							System.out.println("Invalid command to change directory - try 'cd Dir'");
						}
					}

				}
				else if(option.equals("Change server current dir")) { //6
					username = in.readUTF();
					dirCurrentServer = in.readUTF();
					String cmd = in.readUTF();
					String[] cmdSplit = cmd.split(" ");
					if(cmd.equals("cd")) {
						out.writeUTF(dirCurrentServer);
					}
					else {
						if(cmdSplit[0].equals("cd")) {
							if(cmdSplit[1] != null) {
								if(cmdSplit[1].equals("..") || cmdSplit[1].equals("home")) {
									//return to home dir
									out.writeUTF(System.getProperty("user.dir")+"\\Server\\Clients\\" + username);
								}
								else {
									for(int i = 1; i < cmdSplit.length ; i++) {
										File checkDir = new File(dirCurrentServer + "\\" + cmdSplit[i]);
										if(checkDir.exists()) {
											dirCurrentServer = checkDir.getPath(); //This will be used to then list the files only in this directory
											out.writeUTF(checkDir.getPath());
										}
										else {
											out.writeUTF(dirCurrentServer);
											System.out.println("Dir does not exists");
										}
									}
								}

							}else {
								out.writeUTF(dirCurrentServer);
								System.out.println(dirCurrentServer);
							}

						}
						else {
							out.writeUTF(dirCurrentServer);
							System.out.println("Invalid command to change directory - try 'cd Dir'");
						}
					}
				}

			}

		} catch (EOFException e) {
			System.out.println("EOF:" + e);
		} catch (IOException e) {
			System.out.println("IO:" + e);
		}
		// write all users in users.txt in THE END
		try {
			int i = 0;
			FileWriter myWriter = new FileWriter(System.getProperty("user.dir")+"\\Server\\users.txt");
			while (i < users.size()-1 && users.get(i) != null) {
				myWriter.write(users.get(i).username + "," + users.get(i).password + ","
						+ users.get(i).phone + "," + users.get(i).address + "," + users.get(i).expiration + ","
						+ users.get(i).college + "\n");
				i++;
			}
			myWriter.write(users.get(i).username + "," + users.get(i).password + ","
					+ users.get(i).phone + "," + users.get(i).address + "," + users.get(i).expiration + ","
					+ users.get(i).college);
			myWriter.close();

			int j = 0;
			FileWriter myWriterSec = new FileWriter(System.getProperty("user.dir")+"\\Server_sec\\users.txt");
			while (j < users.size()-1 && users.get(j) != null) {
				myWriterSec.write(users.get(j).username + "," + users.get(j).password + ","
						+ users.get(j).phone + "," + users.get(j).address + "," + users.get(j).expiration + ","
						+ users.get(j).college + "\n");
				j++;
			}
			myWriterSec.write(users.get(j).username + "," + users.get(j).password + ","
					+ users.get(j).phone + "," + users.get(j).address + "," + users.get(j).expiration + ","
					+ users.get(j).college);
			myWriterSec.close();

		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

	private void copyFile(Path path, Path path2) throws IOException{
		Files.copy(path, path2);

	}

	public User FindUser(String username, String password) {
		int i = 0;
		User loggedUser = new User("","","","","","");
		while (i < users.size() && users.get(i) != null) {
			if (username.equals(users.get(i).username) && password.equals(users.get(i).password)) {
				loggedUser.username=username;
				loggedUser.password=password;
				loggedUser.phone=users.get(i).phone;
				loggedUser.address=users.get(i).address;
				loggedUser.expiration=users.get(i).expiration;
				loggedUser.college=users.get(i).college;
				return loggedUser;
			}
			i++;
		}

		return null;
	}

	public boolean changePassword(String username, String password, String oldPass, String newPass) {
		int i = 0;
		if(!password.equals(oldPass)) {
			System.out.println("Unable to change password");
			return false;
		}else {
			if(!oldPass.equals(newPass)) {
				while (i < users.size() && users.get(i) != null) {
					if (username.equals(users.get(i).username) && oldPass.equals(users.get(i).password)) {
						users.get(i).password = newPass;
						System.out.println("User "+username+" password successfully changed");
						System.out.println("Old Password: "+oldPass);
						System.out.println("New Password: "+newPass);
						return true;
					}
					i++;
				}
			}
			else {
				System.out.println("New password entered is the same as the old one");
				return false;
			}
		}

		return false;

	}

	public boolean RegisterUser(String username, String password, String phone, String address, String expiration,String college) {
		// try {
		if (CheckUser(username, "")) { // user already exists
			return false;
		} else {
			User newUser = new User(username, password, phone, address, expiration, college); // new user (object)
			users.add(newUser);
			return true;
		}

	}

	public boolean CheckUser(String username, String password) {
		int i = 0;
		while (i < users.size() && users.get(i) != null) {
			if (username.equals(users.get(i).username) && password.equals(users.get(i).password)) {
				return true;
			} else if (username.equals(users.get(i).username) && password.equals("")) {
				return true;
			}
			i++;
		}

		return false;
	}
}