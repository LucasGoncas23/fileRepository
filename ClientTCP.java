import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientTCP {
	private static String dirCurrentClient;
	private static String dirCurrentServer;
	private static int countC=0;
	private static int countS=0;
	
	public static void main(String[] args) {
		//ArrayList<String> filesList = new ArrayList<String>();
		// args[0] <- hostname of destination
		if (args.length == 0) {
			System.out.println("java TCPClient hostname");
			System.exit(0);
		}

		// 1o passo - criar socket
		int serversocket = 7000;
		try (Socket s = new Socket(args[0], serversocket)) {
			System.out.println("SOCKET=" + s);
			
			// 2o passo
			DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			
			// 3o passo
			try (Scanner sc = new Scanner(System.in)) {
				boolean logged = false;
				boolean passChange;
				String username;
				String pass;
				String contact;
				String address;
				String expiration;
				String college;
				String oldPass;
				String newPass;
				User loggedUser = new User("","");
				label:
				while (true) {
					if(logged) {		//CLIENT MENU

						System.out.println("-------------USER MENU-------------");
						System.out.println("1 - Change password");
						System.out.println("2 - List client's files");
						System.out.println("3 - List server's files");
						System.out.println("4 - Setup address and ports for primary and secondary servers");
						System.out.println("5 - Change client's current dir");
						System.out.println("6 - Change server's current dir");
						System.out.println("7 - Download file from server to client");
						System.out.println("8 - Load a file to server");
						System.out.println("9 - Logout");
						String optionClient = sc.nextLine();
						switch (optionClient) {
							case "1":
								out.writeUTF("Change password");
								System.out.println("Old Password: ");
								oldPass = sc.nextLine();
								System.out.println("New Password: ");
								newPass = sc.nextLine();
								out.writeUTF(loggedUser.username);
								out.writeUTF(loggedUser.password);
								out.writeUTF(oldPass);
								out.writeUTF(newPass);
								passChange = in.readBoolean();
								if (passChange) {
									System.out.println("User's Password " + loggedUser.username + " successfully changed");
								} else {
									System.out.println("Unable to change password");
								}
								logged = false;

								break;
							//List Client's files
							case "2": {
								out.writeUTF("List client files");
								out.writeUTF(loggedUser.username);
								out.writeUTF(loggedUser.password);
								if (countC == 0) {
									out.writeUTF(System.getProperty("user.dir")+"\\" + loggedUser.username + "Files");
								} else {
									out.writeUTF(dirCurrentClient);
								}
								System.out.println("Local client's files: ");
								int len = in.readInt();
								for (int i = 0; i < len; i++) {
									System.out.println(in.readUTF());
								}

								break;
							}
							//List Servers's files
							case "3": {
								out.writeUTF("List server files");
								out.writeUTF(loggedUser.username);
								out.writeUTF(loggedUser.password);
								if (countS == 0) {
									out.writeUTF(System.getProperty("user.dir")+"\\Server\\Clients\\" + loggedUser.username);
								} else {
									out.writeUTF(dirCurrentServer);
								}
								System.out.println("Client's files in the server: ");
								int len = in.readInt();
								for (int i = 0; i < len; i++) {
									System.out.println(in.readUTF());
								}
								break;
							}
							//Setup address and ports for primary and secondary servers
							case "4":

								break;
							//Change client's current dir
							case "5": {
								out.writeUTF("Change client current dir");
								out.writeUTF(loggedUser.username);
								if (countC == 0) {
									dirCurrentClient = System.getProperty("user.dir") + "\\" + loggedUser.username + "Files";
								}

								out.writeUTF(dirCurrentClient);
								System.out.println("Current client dir = " + dirCurrentClient);
								System.out.println("Insert command to change dir: ");
								String cmd = sc.nextLine();
								out.writeUTF(cmd);

								dirCurrentClient = in.readUTF();
								System.out.println(dirCurrentClient);
								countC = 1;

								break;
							}
							//Change server's current dir
							case "6": {
								out.writeUTF("Change server current dir");
								out.writeUTF(loggedUser.username);
								if (countS == 0) {
									dirCurrentServer = System.getProperty("user.dir") + "\\Server\\Clients\\" + loggedUser.username;
								}

								out.writeUTF(dirCurrentServer);
								System.out.println("Current server dir = " + dirCurrentServer);
								System.out.println("Insert command to change dir: ");
								String cmd = sc.nextLine();
								out.writeUTF(cmd);

								dirCurrentServer = in.readUTF();
								System.out.println(dirCurrentServer);
								countS = 1;
								break;
							}

							//Download file from server to client
							case "7": {
								out.writeUTF("Download a file from the server");
								out.writeUTF(loggedUser.username);
								if (countC == 0) {
									out.writeUTF(System.getProperty("user.dir")+"\\Server\\Clients\\" + loggedUser.username);
								} else {
									out.writeUTF(dirCurrentServer);
								}
								String file;
								System.out.println("File's name?");
								file = sc.nextLine();
								out.writeUTF(file);

								break;
							}

							//Load a file to server
							case "8": {
								out.writeUTF("Upload a file to the server");
								out.writeUTF(loggedUser.username);
								if (countC == 0) {
									out.writeUTF(System.getProperty("user.dir")+"\\" + loggedUser.username + "Files");
								} else {
									out.writeUTF(dirCurrentClient);
								}
								String file;
								System.out.println("File's name?");
								file = sc.nextLine();
								out.writeUTF(file);
								break;
							}
//logout
							case "9":
								logged = false;
								break;
							default:
								System.out.println("'"+optionClient + "' is an invalid command.");
								break;
						}
						
					}
					else { //START MENU
						System.out.println("--------FUNCTIONALITIES--------");
						System.out.println("1 - Register user");
						System.out.println("2 - Login");
						System.out.println("3 - Bye");
						String option = sc.nextLine();

						switch (option) {
							case "1":
								out.writeUTF(option);
								// READ STRING FROM KEYBOARD
								System.out.println("Username: ");
								username = sc.nextLine();
								//username = Normalizer.normalize(username, Normalizer.Form.NFKD).replaceAll("[^\\p{ASCII}]", "");
								System.out.println("Password: ");
								pass = sc.nextLine();
								System.out.println("Phone: ");
								contact = sc.nextLine();
								System.out.println("Address: ");
								address = sc.nextLine();
								System.out.println("Expiration date CC: ");
								expiration = sc.nextLine();
								System.out.println("College: ");
								college = sc.nextLine();
								out.writeUTF(username);
								out.writeUTF(pass);
								out.writeUTF(contact);
								out.writeUTF(address);
								out.writeUTF(expiration);
								out.writeUTF(college);
								logged = in.readBoolean();
								if (logged) {
									System.out.println("User " + username + " successfully registered");
								} else {
									System.out.println("User " + username + " already exists");
								}
								break;
							case "2":
								out.writeUTF(option);
								// READ STRING FROM KEYBOARD
								System.out.println("Username: ");
								username = sc.nextLine();
								//username = Normalizer.normalize(username, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
								System.out.println("Password: ");
								pass = sc.nextLine();
								out.writeUTF(username);
								out.writeUTF(pass);
								logged = in.readBoolean();
								if (logged) {
									loggedUser.username = username;
									loggedUser.password = pass;
									System.out.println("User " + username + " authenticated");
								} else {
									System.out.println("Wrong username and/or password");
								}

								break;
							case "3":
								out.writeUTF(option);
								break label;
							default:
								System.out.println("'"+option + "' is an invalid command.");
								break;
						}
					}
					
				}
			}
			
		} catch (UnknownHostException e) {
			System.out.println("Sock:" + e.getMessage());
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}
	}


	
	
}