package org.jake;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jake.utils.JakeUtilsFile;
import org.jake.utils.JakeUtilsIO;
import org.jake.utils.JakeUtilsIterable;
import org.jake.utils.JakeUtilsString;
import org.jake.utils.JakeUtilsTime;

/**
 * Main class for launching Jake from command line.
 * 
 * @author Jerome Angibaud
 */
class Main {

	public static void main(String[] args) {
		final long start = System.nanoTime();
		displayIntro();
		JakeLog.info("Java Home : " + System.getProperty("java.home"));
		JakeLog.info("Java Version : " + System.getProperty("java.version")+ ", " + System.getProperty("java.vendor"));
		JakeLog.info("Jake class path : " + System.getProperty("java.class.path"));
		JakeLog.info("Command line : " + JakeUtilsString.join(Arrays.asList(args), " "));
		final Map<String, String> optionMap = new HashMap<String, String>();
		optionMap.putAll(loadOptionsProperties());
		final CommandLine commandLine = CommandLine.of(args);
		optionMap.putAll(commandLine.getSubProjectBuildOptions());
		optionMap.putAll(commandLine.getMasterBuildOptions() );
		JakeOptions.init(optionMap);
		JakeLog.info("Using global options : " + JakeOptions.fieldOptionsToString(JakeOptions.instance()));
		JakeLog.info("And free form options : " + JakeOptions.freeFormToString());
		defineSystemProps(args);
		final File workingDir = JakeUtilsFile.workingDir();
		final Project project = new Project(workingDir);
		JakeLog.nextLine();
		try {
			project.execute(commandLine, JakeOptions.buildClass());
			final int lenght = printAscii(false, "succes.ascii");
			System.out.println(JakeUtilsString.repeat(" ", lenght) + "Total build time : "
					+ JakeUtilsTime.durationInSeconds(start) + " seconds.");
		} catch (final RuntimeException e) {
			System.err.println();
			e.printStackTrace(System.err);
			final int lenght = printAscii(true, "failed.ascii");
			System.err.println(JakeUtilsString.repeat(" ", lenght) + "Total build time : "
					+ JakeUtilsTime.durationInSeconds(start) + " seconds.");
			System.exit(1);
		}
	}



	private static void defineSystemProps(String[] args) {
		for (final String arg : args) {
			if (arg.startsWith("-D")) {
				final int equalIndex = arg.indexOf("=");
				if (equalIndex <= -1) {
					System.setProperty(arg.substring(2), "");
				} else {
					final String name = arg.substring(2, equalIndex);
					final String value = arg.substring(equalIndex+1);
					System.setProperty(name, value);
				}
			}
		}
	}

	private static Map<String, String> loadOptionsProperties() {
		final File propFile = new File(JakeLocator.jakeHome(), "options.properties");
		if (propFile.exists()) {
			final Properties properties = JakeUtilsFile.readPropertyFile(propFile);
			final File userPropFile = new File(JakeLocator.jakeUserHome(), "options.properties");
			if (userPropFile.exists()) {
				final Properties userProperties = JakeUtilsFile.readPropertyFile(propFile);
				properties.putAll(userProperties);
			}
			final Map<String, String> result = JakeUtilsIterable.propertiesToMap(properties);
			return result;
		}
		return Collections.emptyMap();

	}

	private static int printAscii(boolean error, String fileName) {
		final InputStream inputStream = Main.class.getResourceAsStream(fileName);
		final List<String> lines = JakeUtilsIO.readLines(inputStream);
		int i = 0;
		for (final String line: lines) {
			if (i < line.length()) {
				i = line.length();
			}
			if (error) {
				System.err.println(line);
			} else {
				System.out.println(line);
			}
		}
		return i;
	}

	private static void displayIntro() {
		final int lenght = printAscii(false, "jake.ascii");
		JakeLog.info(JakeUtilsString.repeat(" ", lenght) + "The 100% Java build system.");
		final String version = JakeUtilsIO.readResourceIfExist("org/jake/version.txt");
		if (version != null) {
			JakeLog.info(JakeUtilsString.repeat(" ", lenght) + "Version : " + version);
		}
		JakeLog.nextLine();
	}

	private Main() {}

}
