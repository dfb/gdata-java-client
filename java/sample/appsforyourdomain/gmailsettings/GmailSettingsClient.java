/* Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package sample.appsforyourdomain.gmailsettings;

import sample.util.SimpleCommandLineParser;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This is the command line client for the Google Apps Gmail Settings API. 
 */
public class GmailSettingsClient {

  /**
   * Prevents the class from being instantiated.
   */
  private GmailSettingsClient() {}

  /**
   * Prints the command line usage of this sample application.
   */
  private static void printUsageAndExit() {
    System.out.println("Usage: java GmailSettingsClient"
        + " --username <username> --password <password> --domain <domain>\n"
        + " --setting <setting> [--disable]"
        + " [--get true --destination_user <destination_user>] ");

    System.out.println();
    System.out.println("A simple application that demonstrates how to get or"
        + " change Gmail settings in a Google Apps email account."
        + " Authenticates using the provided login credentials, then retrieves"
        + " or modifies the settings of the specified account.");
    System.out.println();
    System.out.println("Specify username and destination_user as just the name,"
        + " not the email address.  For example, to change settings for"
        + " joe@example.com use these options:  --username joe --password"
        + " your_password --domain example.com");
    System.out.println();
    System.out.println("**For changing settings...");
    System.out.println("Select which setting to change with the setting flag."
        + " For example, to change the POP3 settings, use --setting pop"
        + " (allowed values are filter, sendas, label, forwarding, pop, imap,"
        + " vacation, signature, general, language, and webclip.)");
    System.out.println();
    System.out.println("By default the selected setting will be enabled, "
        + "but with the --disable flag it will be disabled.");
    System.out.println();
    System.out.println("**For retrieving settings...");
    System.out.println("To retrieve settings, use the --get=true option and"
        + " mandatorily specify a single --destination_user."
        + " For example, to get the signature settings, use"
        + " --get true --settings signature --destination_user joe"
        + " (allowed values are label, sendas, forwarding, pop, imap, vacation,"
        + " and signature).");
    System.out.println();

    System.exit(1);
  }

  /**
   * Main entry point. Parses arguments and creates and invokes the
   * GmailSettingsClient
   *
   * Usage: java GmailSettingsClient --username &lt;user&gt;
   * --password &lt;pass&gt; --domain &lt;domain&gt; --setting &lt;setting&gt;
   * [--get true --destination_user &lt;destination_user&gt;] [--disable]
   *
   * &lt;setting&gt; should be one of:
   * <ul>
   *   <li>filter</li>
   *   <li>sendas<li>
   *   <li>label</li>
   *   <li>forwarding</li>
   *   <li>pop</li>
   *   <li>imap</li>
   *   <li>vacation</li>
   *   <li>signature</li>
   *   <li>general</li>
   *   <li>language</li>
   *   <li>webclip</li>
   * </ul>
   */
  public static void main(String[] arg) {
    SimpleCommandLineParser parser = new SimpleCommandLineParser(arg);

    // Parse command-line flags
    String username = parser.getValue("username");
    String password = parser.getValue("password");
    String domain = parser.getValue("domain");
    String destinationUser = parser.getValue("destination_user");
    String setting = parser.getValue("setting");
    String get = parser.getValue("get");

    boolean help = parser.containsKey("help");
    boolean enable = !parser.containsKey("disable");
    boolean doGet = (get != null && get.equalsIgnoreCase("true"));
    if (help || (username == null) || (password == null) || (domain == null)
        || (setting == null) || (doGet && destinationUser == null)) {
      printUsageAndExit();
    }

    // --setting flag is quite accepting - case-insensitive and startsWith
    // mean that not just "pop" but also "POP3" works
    setting = setting.trim().toLowerCase();

    try {
      GmailSettingsService settings = new GmailSettingsService("exampleCo-exampleApp-1", domain,
          username, password);

      List<String> users = new ArrayList<String>();
      users.add(destinationUser);
      
      if (setting.startsWith("filter")) {
        if (doGet) {
          System.out.println("Retrieving filter settings is not supported.\n");
          printUsageAndExit();

        } else {
          settings.createFilter(users,
              Defaults.FILTER_FROM,
              Defaults.FILTER_TO,
              Defaults.FILTER_SUBJECT,
              Defaults.FILTER_HAS_THE_WORD,
              Defaults.FILTER_DOES_NOT_HAVE_THE_WORD,
              Defaults.FILTER_HAS_ATTACHMENT,
              Defaults.FILTER_SHOULD_MARK_AS_READ,
              Defaults.FILTER_SHOULD_ARCHIVE,
              Defaults.FILTER_LABEL);
        }
        
      } else if (setting.startsWith("sendas")) {
        if (doGet) {
          List<Map<String, String>> sendAsSettings = settings.retrieveSendAs(destinationUser);
          int count = 0;
          for (Map<String, String> sendAsSetting : sendAsSettings) {
            System.out.println("sendAs setting " + ++count + ":");
            Set<Entry<String, String>> entries = sendAsSetting.entrySet();
            for (Entry<String, String> entry : entries)
              System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
          }

        } else {
          settings.createSendAs(users, Defaults.SEND_AS_NAME, Defaults.SEND_AS_ADDRESS,
              Defaults.SEND_AS_REPLY_TO, Defaults.SEND_AS_MAKE_DEFAULT);
        }
        
      } else if (setting.startsWith("label")) {
        if (doGet) {
          List<Map<String, String>> labels = settings.retrieveLabels(destinationUser);
          int count = 0;
          for (Map<String, String> label : labels) {
            System.out.println("label " + ++count + ":");
            Set<Entry<String, String>> entries = label.entrySet();
            for (Entry<String, String> entry : entries)
              System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
          }

        } else {
          settings.createLabel(users, Defaults.LABEL);
        }
        
      } else if (setting.startsWith("forwarding")) {
        if (doGet) {
          Map<String, String> forwarding = settings.retrieveForwarding(destinationUser);
          System.out.println("forwarding settings:");
          for (Entry<String, String> entry : forwarding.entrySet())
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());

        } else {
          settings.changeForwarding(users, Defaults.FORWARDING_ENABLE,
              Defaults.FORWARDING_FORWARD_TO, Defaults.FORWARDING_ACTION);
        }
        
      } else if (setting.startsWith("pop")) {
        if (doGet) {
          Map<String, String> pop = settings.retrievePop(destinationUser);
          System.out.println("pop settings:");
          for (Entry<String, String> entry : pop.entrySet())
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());

        } else {
          settings.changePop(
              users, Defaults.POP_ENABLE, Defaults.POP_ENABLE_FOR, Defaults.POP_ACTION);
        }
        
      } else if (setting.startsWith("imap")) {
        if (doGet) {
          boolean imap = settings.retrieveImap(destinationUser);
          System.out.println("imap settings:");
          System.out.println("\tenabled: " + imap);

        } else {
          settings.changeImap(users, Defaults.IMAP_ENABLE);
        }
        
      } else if (setting.startsWith("vacation")) {
        if (doGet) {
          Map<String, String> vacation = settings.retrieveVacation(destinationUser);
          System.out.println("vacation settings:");
          for (Entry<String, String> entry : vacation.entrySet())
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());

        } else {
          settings.changeVacation(users, Defaults.VACATION_ENABLE, Defaults.VACATION_SUBJECT,
              Defaults.VACATION_MESSAGE, Defaults.VACATION_CONTACTS_ONLY);
        }
        
      } else if (setting.startsWith("signature")) {
        if (doGet) {
          String signature = settings.retrieveSignature(destinationUser);
          System.out.println("signature:");
          System.out.println("\tvalue: " + signature);

        } else {
          settings.changeSignature(users, Defaults.SIGNATURE);
        }
        
      } else if (setting.startsWith("general")) {
        if (doGet) {
          System.out.println("Retrieving general settings is not supported.\n");
          printUsageAndExit();

        } else {
          settings.changeGeneral(users,
              Defaults.GENERAL_PAGE_SIZE,
              Defaults.GENERAL_ENABLE_SHORTCUTS,
              Defaults.GENERAL_ENABLE_ARROWS,
              Defaults.GENERAL_ENABLE_SNIPPETS,
              Defaults.GENERAL_ENABLE_UNICODE);
        }
        
      } else if (setting.startsWith("language")) {
        if (doGet) {
          System.out.println("Retrieving language settings is not supported.\n");
          printUsageAndExit();

        } else {
          settings.changeLanguage(users, Defaults.LANGUAGE);
        }
        
      } else if (setting.startsWith("webclip")) {
        if (doGet) {
          System.out.println("Retrieving webclip settings is not supported.\n");
          printUsageAndExit();

        } else {
          settings.changeWebClip(users, Defaults.WEBCLIP_ENABLE);
        }
        
      } else {
        printUsageAndExit();
      }
    } catch (AuthenticationException e) {
      System.err.println(e);
    } catch (IllegalArgumentException e) {
      System.err.println(e);
    } catch (ServiceException e) {
      System.err.println(e);
    } catch (MalformedURLException e) {
      System.err.println(e);
    } catch (IOException e) {
      System.err.println(e);
    }
  }
}
