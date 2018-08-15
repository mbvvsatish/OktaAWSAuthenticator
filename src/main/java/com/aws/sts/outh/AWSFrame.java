package com.aws.sts.outh;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.xml.datatype.XMLGregorianCalendar;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithSAMLRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithSAMLResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.aws.sts.auth.utils.DateUtils;
import com.aws.sts.auth.utils.MetaData;
import com.aws.sts.outh.okta.OktaSaml;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AWSFrame extends JFrame {
	
	static final String ACCES_KEY_ID = "aws_access_key_id";
	static final String SECRET_ACCESS_KEY = "aws_secret_access_key";
	static final String SESSION_TOKEN = "aws_session_token";
	static final String CONFIG_OUTPUT = "output";
	static final String CONFIG_REGION = "region";
	static final String APP_ROOT_DIRECTORY = "OktaAws";

    
	private JTextField idpUrl;
	private JTextField user;
	private JPasswordField pwd;
	private JTextField oktaAwsAppUrl;
	private JTextField idpArn;
	private JTextField roleArn;
	private JTextField region;
	SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
	private JTextField txtProfile;
	private JTextField txtEc2;
	private JCheckBox chkBoxHostEc2;
	Environment env = null;
	
	JComponent logComponent;
	String userHome = System.getProperty("user.home");
	String oktaawsdir = File.separator+".oktaaws"+File.separator;
	String serializedFile = oktaawsdir+"env.ser";
	String awsCredentialsFile = File.separator+".aws"+File.separator+"credentials";
	String awsConfigFile = File.separator+".aws"+File.separator+"config";
	String apacheDocRoot = "/Library/WebServer/Documents";
	

	@SuppressWarnings("restriction")
	public AWSFrame() throws HeadlessException {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setSize(700, 400);
		this.setResizable(false);
		ImageIcon img = new ImageIcon("key.png");
		setIconImage(img.getImage());
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		        setExtendedState(JFrame.ICONIFIED);
		    }
		});
		
		JLabel lblUser = new JLabel("User");
		lblUser.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GridBagConstraints gbc_lblUser = new GridBagConstraints();
		gbc_lblUser.anchor = GridBagConstraints.EAST;
		gbc_lblUser.insets = new Insets(0, 0, 5, 5);
		gbc_lblUser.gridx = 1;
		gbc_lblUser.gridy = 1;
		getContentPane().add(lblUser, gbc_lblUser);
		
		user = new JTextField();
		user.setFont(new Font("Dialog", Font.PLAIN, 10));
		GridBagConstraints gbc_user = new GridBagConstraints();
		gbc_user.insets = new Insets(0, 0, 5, 5);
		gbc_user.fill = GridBagConstraints.HORIZONTAL;
		gbc_user.gridx = 2;
		gbc_user.gridy = 1;
		getContentPane().add(user, gbc_user);
		user.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("okta url");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 4;
		gbc_lblNewLabel.gridy = 1;
		getContentPane().add(lblNewLabel, gbc_lblNewLabel);
		
		idpUrl = new JTextField();
		idpUrl.setFont(new Font("Dialog", Font.PLAIN, 10));
		GridBagConstraints gbc_idpUrl = new GridBagConstraints();
		gbc_idpUrl.gridwidth = 8;
		gbc_idpUrl.insets = new Insets(0, 0, 5, 5);
		gbc_idpUrl.fill = GridBagConstraints.HORIZONTAL;
		gbc_idpUrl.gridx = 5;
		gbc_idpUrl.gridy = 1;
		getContentPane().add(idpUrl, gbc_idpUrl);
		idpUrl.setColumns(10);
		idpUrl.setText("<company-name>.okta.com");
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 1;
		gbc_lblPassword.gridy = 2;
		getContentPane().add(lblPassword, gbc_lblPassword);
		
		pwd = new JPasswordField();
		pwd.setFont(new Font("Dialog", Font.PLAIN, 10));
		GridBagConstraints gbc_pwd = new GridBagConstraints();
		gbc_pwd.insets = new Insets(0, 0, 5, 5);
		gbc_pwd.fill = GridBagConstraints.HORIZONTAL;
		gbc_pwd.gridx = 2;
		gbc_pwd.gridy = 2;
		getContentPane().add(pwd, gbc_pwd);
		
		JLabel lblOktaAwsApp = new JLabel("Okta AWS App");
		lblOktaAwsApp.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GridBagConstraints gbc_lblOktaAwsApp = new GridBagConstraints();
		gbc_lblOktaAwsApp.insets = new Insets(0, 0, 5, 5);
		gbc_lblOktaAwsApp.gridx = 4;
		gbc_lblOktaAwsApp.gridy = 2;
		getContentPane().add(lblOktaAwsApp, gbc_lblOktaAwsApp);
		
		oktaAwsAppUrl = new JTextField();
		oktaAwsAppUrl.setFont(new Font("Dialog", Font.PLAIN, 10));
		GridBagConstraints gbc_oktaAwsAppUrl = new GridBagConstraints();
		gbc_oktaAwsAppUrl.gridwidth = 8;
		gbc_oktaAwsAppUrl.insets = new Insets(0, 0, 5, 5);
		gbc_oktaAwsAppUrl.fill = GridBagConstraints.HORIZONTAL;
		gbc_oktaAwsAppUrl.gridx = 5;
		gbc_oktaAwsAppUrl.gridy = 2;
		getContentPane().add(oktaAwsAppUrl, gbc_oktaAwsAppUrl);
		oktaAwsAppUrl.setColumns(10);
		oktaAwsAppUrl.setText("AWS App Location path in SAML Auth provider");
		
		JCheckBox chckbxSaveCredentials = new JCheckBox("Save Credentials");
		chckbxSaveCredentials.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GridBagConstraints gbc_chckbxSaveCredentials = new GridBagConstraints();
		gbc_chckbxSaveCredentials.anchor = GridBagConstraints.WEST;
		gbc_chckbxSaveCredentials.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxSaveCredentials.gridx = 2;
		gbc_chckbxSaveCredentials.gridy = 3;
		getContentPane().add(chckbxSaveCredentials, gbc_chckbxSaveCredentials);
		chckbxSaveCredentials.setSelected(true);
		
		JLabel lblIpdArn = new JLabel("IDP Arn");
		lblIpdArn.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GridBagConstraints gbc_lblIpdArn = new GridBagConstraints();
		gbc_lblIpdArn.anchor = GridBagConstraints.EAST;
		gbc_lblIpdArn.insets = new Insets(0, 0, 5, 5);
		gbc_lblIpdArn.gridx = 4;
		gbc_lblIpdArn.gridy = 3;
		getContentPane().add(lblIpdArn, gbc_lblIpdArn);
		
		idpArn = new JTextField();
		idpArn.setFont(new Font("Dialog", Font.PLAIN, 10));
		GridBagConstraints gbc_idpArn = new GridBagConstraints();
		gbc_idpArn.gridwidth = 8;
		gbc_idpArn.insets = new Insets(0, 0, 5, 5);
		gbc_idpArn.fill = GridBagConstraints.HORIZONTAL;
		gbc_idpArn.gridx = 5;
		gbc_idpArn.gridy = 3;
		getContentPane().add(idpArn, gbc_idpArn);
		idpArn.setColumns(10);
		idpArn.setText("arn:aws:iam::<aws-account-id>:saml-provider/OktaIDP");
		
		JCheckBox chckbxAutoRefresh = new JCheckBox("Auto Refresh");
		chckbxAutoRefresh.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GridBagConstraints gbc_chckbxAutoRefresh = new GridBagConstraints();
		gbc_chckbxAutoRefresh.anchor = GridBagConstraints.WEST;
		gbc_chckbxAutoRefresh.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxAutoRefresh.gridx = 2;
		gbc_chckbxAutoRefresh.gridy = 4;
		getContentPane().add(chckbxAutoRefresh, gbc_chckbxAutoRefresh);
		chckbxAutoRefresh.setSelected(true);
		
		JLabel lblRoleArn = new JLabel("Role Arn");
		lblRoleArn.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GridBagConstraints gbc_lblRoleArn = new GridBagConstraints();
		gbc_lblRoleArn.anchor = GridBagConstraints.EAST;
		gbc_lblRoleArn.insets = new Insets(0, 0, 5, 5);
		gbc_lblRoleArn.gridx = 4;
		gbc_lblRoleArn.gridy = 4;
		getContentPane().add(lblRoleArn, gbc_lblRoleArn);
		
		roleArn = new JTextField();
		roleArn.setFont(new Font("Dialog", Font.PLAIN, 10));
		GridBagConstraints gbc_roleArn = new GridBagConstraints();
		gbc_roleArn.gridwidth = 8;
		gbc_roleArn.insets = new Insets(0, 0, 5, 5);
		gbc_roleArn.fill = GridBagConstraints.HORIZONTAL;
		gbc_roleArn.gridx = 5;
		gbc_roleArn.gridy = 4;
		getContentPane().add(roleArn, gbc_roleArn);
		roleArn.setColumns(10);
		roleArn.setText("arn:aws:iam::<aws-account-id>:role/<saml-role-name>");
		
		JButton btnAuthenticate = new JButton("Authenticate");
		
		GridBagConstraints gbc_btnAuthenticate = new GridBagConstraints();
		gbc_btnAuthenticate.insets = new Insets(0, 0, 5, 5);
		gbc_btnAuthenticate.gridx = 2;
		gbc_btnAuthenticate.gridy = 5;
		getContentPane().add(btnAuthenticate, gbc_btnAuthenticate);
		
		JLabel lblRegion = new JLabel("Region");
		lblRegion.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GridBagConstraints gbc_lblRegion = new GridBagConstraints();
		gbc_lblRegion.anchor = GridBagConstraints.EAST;
		gbc_lblRegion.insets = new Insets(0, 0, 5, 5);
		gbc_lblRegion.gridx = 4;
		gbc_lblRegion.gridy = 5;
		getContentPane().add(lblRegion, gbc_lblRegion);
		
		region = new JTextField();
		region.setFont(new Font("Dialog", Font.PLAIN, 10));
		region.setText("us-east-1");
		GridBagConstraints gbc_region = new GridBagConstraints();
		gbc_region.gridwidth = 8;
		gbc_region.insets = new Insets(0, 0, 5, 5);
		gbc_region.fill = GridBagConstraints.HORIZONTAL;
		gbc_region.gridx = 5;
		gbc_region.gridy = 5;
		getContentPane().add(region, gbc_region);
		region.setColumns(10);
		
		JCheckBox chkBoxWriteProfile = new JCheckBox("");
		chkBoxWriteProfile.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GridBagConstraints gbc_chkBoxWriteProfile = new GridBagConstraints();
		gbc_chkBoxWriteProfile.insets = new Insets(0, 0, 5, 5);
		gbc_chkBoxWriteProfile.gridx = 3;
		gbc_chkBoxWriteProfile.gridy = 7;
		getContentPane().add(chkBoxWriteProfile, gbc_chkBoxWriteProfile);
		chkBoxWriteProfile.setSelected(true);
		
		JLabel lblWriteProfile = new JLabel("Write Profile");
		lblWriteProfile.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GridBagConstraints gbc_lblWriteProfile = new GridBagConstraints();
		gbc_lblWriteProfile.insets = new Insets(0, 0, 5, 5);
		gbc_lblWriteProfile.gridx = 4;
		gbc_lblWriteProfile.gridy = 7;
		getContentPane().add(lblWriteProfile, gbc_lblWriteProfile);
		
		txtProfile = new JTextField();
		txtProfile.setFont(new Font("Dialog", Font.PLAIN, 10));
		txtProfile.setText("default");
		GridBagConstraints gbc_txtProfile = new GridBagConstraints();
		gbc_txtProfile.gridwidth = 5;
		gbc_txtProfile.insets = new Insets(0, 0, 5, 5);
		gbc_txtProfile.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtProfile.gridx = 5;
		gbc_txtProfile.gridy = 7;
		getContentPane().add(txtProfile, gbc_txtProfile);
		txtProfile.setColumns(10);
		
		if (File.separator.equals("/")) {
			chkBoxHostEc2 = new JCheckBox("");
			chkBoxHostEc2.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
			GridBagConstraints gbc_chkBoxHostEc2 = new GridBagConstraints();
			gbc_chkBoxHostEc2.insets = new Insets(0, 0, 5, 5);
			gbc_chkBoxHostEc2.gridx = 3;
			gbc_chkBoxHostEc2.gridy = 8;
			getContentPane().add(chkBoxHostEc2, gbc_chkBoxHostEc2);
			chkBoxHostEc2.setSelected(false);
		}
		
		JLabel lblHostEcAmi = new JLabel("Host EC2 AMI");
		lblHostEcAmi.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GridBagConstraints gbc_lblHostEcAmi = new GridBagConstraints();
		gbc_lblHostEcAmi.insets = new Insets(0, 0, 5, 5);
		gbc_lblHostEcAmi.gridx = 4;
		gbc_lblHostEcAmi.gridy = 8;
		getContentPane().add(lblHostEcAmi, gbc_lblHostEcAmi);
		
		txtEc2 = new JTextField();
		txtEc2.setFont(new Font("Dialog", Font.PLAIN, 10));
		txtEc2.setText("http://169.254.169.254");
		GridBagConstraints gbc_txtEc2 = new GridBagConstraints();
		gbc_txtEc2.gridwidth = 5;
		gbc_txtEc2.insets = new Insets(0, 0, 5, 5);
		gbc_txtEc2.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEc2.gridx = 5;
		gbc_txtEc2.gridy = 8;
		getContentPane().add(txtEc2, gbc_txtEc2);
		txtEc2.setColumns(10);
		
		JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Dialog", Font.PLAIN, 10));
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridheight = 4;
		gbc_textArea.gridwidth = 11;
		gbc_textArea.insets = new Insets(0, 0, 5, 5);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 2;
		gbc_textArea.gridy = 10;
		JScrollPane scroll = new JScrollPane (textArea, 
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(scroll, gbc_textArea);
		logComponent = textArea;
		
		btnAuthenticate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				env = new Environment(idpUrl.getText(), user.getText(), new String(pwd.getPassword()), 
						chckbxSaveCredentials.isSelected(), chckbxAutoRefresh.isSelected(), oktaAwsAppUrl.getText(), 
						roleArn.getText(), idpArn.getText(), region.getText(), txtProfile.getText(), 
						chkBoxWriteProfile.isSelected(), txtEc2.getText(), ((chkBoxHostEc2==null)?false:chkBoxHostEc2.isSelected()));
				
				//means its just in root directory. not in user home directory
			    if (userHome.length() == 7) {
			    	userHome += env.getUser();
			    }
			    
				Boolean isSaveCreds = env.getSaveCreds();
				
				if (isSaveCreds != null && isSaveCreds) {
					saveCredentials();
				}
				
				//Authenticate with OKTA and get AWS temp credentials
				authenticate(env);
				
				if (env != null && env.getAutoRefresh() != null && env.getAutoRefresh()) {
					//Start a background worker thread that does hearbeat and auto refreshes the temp credentials
					worker.execute();
				}

			}
		});
		
		this.loadCredentials();
		if (env != null) {
			//Load saved credentials on the screen
			user.setText(env.getUser());
			pwd.setText(env.getPwd());
			chckbxSaveCredentials.setSelected((env.getSaveCreds() == null || !env.getSaveCreds())?false:true);
			chckbxAutoRefresh.setSelected((env.getAutoRefresh() == null || !env.getAutoRefresh())?false:true);
			idpUrl.setText(env.getOktaOrg());
			idpArn.setText(env.getIdpArn());
			oktaAwsAppUrl.setText(env.getOktaAwsAppUrl());
			roleArn.setText(env.getRoleArn());
			region.setText(env.getRegion());
			
			txtProfile.setText(env.getProfile());
			txtEc2.setText(env.getEc2MetadataUrl());
			
			if (env.getAutoRefresh() != null && env.getAutoRefresh()) {
				//Start a background worker thread that does hearbeat and auto refreshes the temp credentials
				worker.execute();
			}
		}
		
	}
	
	//Background task for loading images.
    SwingWorker worker = new SwingWorker<Void, Void>() {

		@Override
		protected Void doInBackground() throws Exception {
			if (env != null && env.getAutoRefresh() != null && env.getAutoRefresh()) {
				while (true) {
					loadCredentials();
					if (env == null  || !env.getAutoRefresh()) {
						break;
					}
					long seconds = 0;
					if (env.getExpiryTime() != null) {
						Date expirytime = env.getExpiryTime();
						Date currenttime = Calendar.getInstance().getTime();
						seconds = (expirytime.getTime()-currenttime.getTime())/1000;
					}
					if (seconds <= 300) {
						//Authenticate with OKTA and get AWS temp credentials
						authenticate(env);
						seconds = 3600;
					}
					
					logMessage("Heartbeat..."+(seconds)/60+" Minutes remaining.");
					Thread.sleep(1000*60);
				}
			}
			return null;
		}

		@Override
		protected void done() {
			// TODO Auto-generated method stub
			super.done();
		}
        
    	
    };

	public Credentials authenticate(Environment env) {
		logMessage("Refreshing Credentials...");
		OktaSaml auth = new OktaSaml(env);
		try {
			String samlResponse = auth.getSamlResponse();
			
			AssumeRoleWithSAMLRequest req = new AssumeRoleWithSAMLRequest();
			req.setDurationSeconds(3600);
			req.setPrincipalArn(env.getIdpArn());
			req.setRoleArn(env.getRoleArn());
			req.setSAMLAssertion(samlResponse);

			Regions regions = (env.getRegion() == null || "".equals(env.getRegion()))?Regions.US_EAST_1:Regions.fromName(env.getRegion());
			AWSSecurityTokenService client = AWSSecurityTokenServiceClientBuilder.standard().withRegion(regions).build();
			
			AssumeRoleWithSAMLResult result = client.assumeRoleWithSAML(req);
			Credentials tempCredentials = result.getCredentials();
			
			if (tempCredentials != null) {
				env.setLastRunTime(Calendar.getInstance().getTime());
				Date utcdate = tempCredentials.getExpiration();
				env.setExpiryTime(utcdate);
	
				logMessage("Valid till "+utcdate);
				
				//Save Credential information
				saveCredentials();
				
				//Write to AWS Credential file
				if (env.getWriteProfile() != null && env.getWriteProfile()) {
					writeToAwsCredentialsFile(env.getProfile(), tempCredentials);
				}
				
				if (env.getHostEc2AMi() != null && env.getHostEc2AMi()) {
					updateEC2MetaData(tempCredentials);
				}
				
				/*File currentLocation = new File("oktaaws.txt");
				Runtime.getRuntime().exec("/bin/sh "
						+currentLocation.getAbsolutePath().substring(0, currentLocation.getAbsolutePath().lastIndexOf("/"))
						+"/oktaaws.sh "+tempCredentials.getAccessKeyId()+" "+tempCredentials.getSecretAccessKey());*/
				
				//Runtime.getRuntime().exec("/bin/sh exportcreds.sh "+tempCredentials.getAccessKeyId()+" "+tempCredentials.getSecretAccessKey());
				
				logMessage("Refresh Complete.");
			} else {
				logMessage("Refresh Failed.");
			}

			return tempCredentials;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void logMessage(String message) {
		Date dt = Calendar.getInstance().getTime();
		String dtFormat = sdf.format(dt);
		((JTextArea)logComponent).append(dtFormat+" "+message+"\n");
		((JTextArea)logComponent).repaint();
		((JTextArea)logComponent).setCaretPosition(((JTextArea)logComponent).getText().length());
	}
	
	private void loadCredentials() {
		
		File serFile = new File(userHome+oktaawsdir+serializedFile);
		if(serFile.exists()){
			serFile.lastModified();
			ObjectInputStream objectinputstream = null;
			try {
			    FileInputStream streamIn = new FileInputStream(userHome+oktaawsdir+serializedFile);
			    objectinputstream = new ObjectInputStream(streamIn);
			    env = (Environment) objectinputstream.readObject();
			    
			    //means its just in root directory. not in user home directory
			    if (userHome.length() == 7) {// "/Users/" - same for both windows and mac
			    	userHome += env.getUser();
			    }
			    
			} catch (Exception e) {
			    e.printStackTrace();
			} finally {
			    if(objectinputstream != null){
			        try {
						objectinputstream .close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			    } 
			}
		}
	}
	
	public void saveCredentials() {
		
		try {
			File directory = new File(userHome+oktaawsdir);
			if(!directory.exists()){
				directory.mkdirs();
			}

			 FileOutputStream fout = new FileOutputStream(userHome+serializedFile);
			 ObjectOutputStream oos = new ObjectOutputStream(fout);
			 oos.writeObject(env);
			 fout.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void writeToAwsCredentialsFile(String profileName, Credentials tempCredentials) {
	    
		try {
			String awsCredentialsFilePath = userHome + awsCredentialsFile;

			List<List<String>> profiles = groupCredentialsByProfile(awsCredentialsFilePath);
			//Add or Update new Credentials to the list
			String profile = (profileName == null || "".equals(profileName))?"default":profileName;
			List<String> newprofile = getNewAwsCredentialsProfile(profile, tempCredentials);
			List<List<String>> newprofiles = addOrUpdateCredentialsWithNewProfile(newprofile, profiles, profile);
			
			//Write to aws credentials file
			FileWriter fw = new FileWriter(awsCredentialsFilePath, false);
			try {
				for (List<String> newprof:newprofiles) {
					for(String ln:newprof) {
						StringWriter sw = new StringWriter();
						sw.write(ln+"\n");
						fw.write(sw.toString());
						sw.close();
					}
				}
			} catch (IOException e) {
				logMessage("Exception updating AWS credentials file under User Home. Please run as Administrator.");
			} finally {
				if (fw!=null) {
					fw.close();
				}
			}
			
			
			String awsConfigFilePath = userHome + awsConfigFile;
			
			List<List<String>> configProfiles = groupCredentialsByProfile(awsConfigFilePath);
			//Add or Update new Credentials to the list
			List<String> newConfigProfile = getNewAwsConfigProfile(profile);
			List<List<String>> newConfigProfiles = addOrUpdateCredentialsWithNewProfile(newConfigProfile, configProfiles, profile);
			
			//Write to aws credentials file
			FileWriter fwConfig = new FileWriter(awsConfigFilePath, false);
			try {
				for (List<String> newprof:newConfigProfiles) {
					for(String ln:newprof) {
						StringWriter sw = new StringWriter();
						sw.write(ln+"\n");
						fwConfig.write(sw.toString());
						sw.close();
					}
				}
			} catch (IOException e) {
				logMessage("Exception updating AWS config file under User Home. Please run as Administrator.");
			} finally {
				if (fwConfig!= null) {
					fwConfig.close();
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<String> getNewAwsCredentialsProfile(String profileName, Credentials tempCredentials) {
		List<String> newprofile = new ArrayList<String>();
		newprofile.add("["+profileName+"]");
		newprofile.add(ACCES_KEY_ID+" = "+tempCredentials.getAccessKeyId());
		newprofile.add(SECRET_ACCESS_KEY+" = "+tempCredentials.getSecretAccessKey());
		newprofile.add(SESSION_TOKEN+" = "+tempCredentials.getSessionToken());
		
		return newprofile;
	}
	
	private List<String> getNewAwsConfigProfile(String profileName) {
		List<String> newprofile = new ArrayList<String>();
		newprofile.add("["+profileName+"]");
		newprofile.add(CONFIG_OUTPUT+" = text");
		newprofile.add(CONFIG_REGION+" = "+env.getRegion());
		
		return newprofile;
	}
	
	private List<List<String>> groupCredentialsByProfile(String awsCredentialsFilePath) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(awsCredentialsFilePath));
		List<List<String>> profiles = new ArrayList<List<String>>();
		List<String> profile = null;
		
		//Create a list of Credentials group
		int linenumber = 1;
		for (String line : lines) {
			if (linenumber != 1 && line.startsWith("[")) {
				profiles.add(profile);
			}
			if (line.startsWith("[")) {
				profile = new ArrayList<String>();
				profile.add(line);
			} else {
				profile.add(line);
			}
			linenumber++;
		}
		//add last profile to profiles
		profiles.add(profile);
		return profiles;
	}
	
	private List<List<String>> addOrUpdateCredentialsWithNewProfile(List<String> newprofile, List<List<String>> profiles, String profileName) {
		//Add or Update new Credentials to the list
		List<List<String>> newprofiles = new ArrayList<List<String>>();
		boolean found = false;
		for (List<String> prof : profiles) {
			if (prof.get(0).contains(profileName)) {
				found = true;
				newprofiles.add(newprofile);
			} else {
				newprofiles.add(prof);
			}
		}
		//If not found, write new credentials as last line
		if (!found) {
			newprofiles.add(newprofile);
		}
		return newprofiles;
	}
	
	private void updateEC2MetaData(Credentials tempCredentials) {
		String metadataPath = apacheDocRoot+"/latest/meta-data/iam/security-credentials/";
		String roleName = env.getRoleArn().substring(env.getRoleArn().lastIndexOf("/")+1);
		File metadataFolder = new File(metadataPath);
		if(!metadataFolder.exists()){
			metadataFolder.mkdirs();
		}
		
		MetaData metadata = new MetaData();
		metadata.AccessKeyId=tempCredentials.getAccessKeyId();
		metadata.SecretAccessKey=tempCredentials.getSecretAccessKey();
		metadata.Token=tempCredentials.getSessionToken();
		metadata.Code="Success";
		metadata.Type="AWS-HMAC";
		
		XMLGregorianCalendar lastupdated = DateUtils.dateToXMLGregorianCalendar(env.getLastRunTime(), TimeZone.getTimeZone("EST"));
	    
		metadata.LastUpdated=lastupdated.toString();

		XMLGregorianCalendar expired = DateUtils.dateToXMLGregorianCalendar(env.getExpiryTime(), TimeZone.getTimeZone("EST"));
		
		metadata.Expiration=expired.toString();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(MapperFeature.USE_STD_BEAN_NAMING, false);
		try {
			File roleFile = new File(metadataPath+roleName);

			String jsonStr = mapper.writeValueAsString(metadata);
			PrintWriter out = new PrintWriter(roleFile);
			out.write(jsonStr);
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
	    java.awt.EventQueue.invokeLater(new Runnable() {
	          public void run() {
	               AWSFrame frame = new AWSFrame();
	               frame.setVisible(true);
	              
	          }
	    });
	}
}
