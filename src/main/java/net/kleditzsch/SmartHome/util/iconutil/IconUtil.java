package net.kleditzsch.SmartHome.util.iconutil;

import net.kleditzsch.SmartHome.util.file.FileUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Hilfsfunktionen für Icons
 */
public class IconUtil {

    /**
     * erzeugt eine Liste mit allen Icons (Bilder)
     *
     * @return Liste mit allen Icons (Bilder)
     */
    public static Map<String, String> listIconFiles() throws IOException, URISyntaxException {

        List<String> fileNames = FileUtil.listResourceFolderFileNames("/webserver/static/img/iconset");
        Map<String, String> files = fileNames.stream().collect(Collectors.toMap(fileName -> fileName, filename -> {

            String name = filename.replaceAll("((\\.png)|(\\.jpg)|(\\.jpeg)|(\\.gif))", "");
            List<String> nameParts = Arrays.asList(name.split("\\_"));
            StringBuilder displayName = new StringBuilder();
            String category = "";
            for (int i = 0; i < nameParts.size(); i++) {

                String part = nameParts.get(i);
                if (i == 0) {

                    category = Character.toUpperCase(part.charAt(0)) + part.substring(1);
                } else {

                    displayName.append(" ").append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
                }
            }
            displayName.append(" (").append(category).append(")");
            return displayName.toString();
        }));
        return files.entrySet().stream()
                .sorted((e1, e2) -> e1.getValue().compareToIgnoreCase(e2.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * gibt eine Liste mit allen unterstützen Font Awaesome Icons zurück
     *
     * @return Liste mit allen unterstützen Font Awaesome Icons
     */
    public static List<String> listFonAweasomeIcons() {

        Set<String> icons = new HashSet<>(1500);
        icons.add("pills");
        icons.add("bicycle");
        icons.add("upload");
        icons.add("clipboard outline");
        icons.add("html5");
        icons.add("laptop");
        icons.add("calendar times");
        icons.add("user plus");
        icons.add("building outline");
        icons.add("building");
        icons.add("align right");
        icons.add("amilia");
        icons.add("reddit alien");
        icons.add("music");
        icons.add("location arrow");
        icons.add("exclamation triangle");
        icons.add("first aid");
        icons.add("empire");
        icons.add("file code");
        icons.add("share alternate square");
        icons.add("square full");
        icons.add("map outline");
        icons.add("arrows alternate horizontal");
        icons.add("gem outline");
        icons.add("chevron left");
        icons.add("google play");
        icons.add("won sign");
        icons.add("google");
        icons.add("compass outline");
        icons.add("strava");
        icons.add("codepen");
        icons.add("hourglass end");
        icons.add("clockwise rotated cloud");
        icons.add("folder");
        icons.add("stop");
        icons.add("bottom left corner add");
        icons.add("volume off");
        icons.add("utensil spoon");
        icons.add("fast forward");
        icons.add("newspaper");
        icons.add("eraser");
        icons.add("steam square");
        icons.add("volume up");
        icons.add("angle double up");
        icons.add("caret down");
        icons.add("google wallet");
        icons.add("skype");
        icons.add("thumbs up outline");
        icons.add("chess rook");
        icons.add("quora");
        icons.add("outdent");
        icons.add("close");
        icons.add("file pdf");
        icons.add("slack hash");
        icons.add("chart pie");
        icons.add("band aid");
        icons.add("buysellads");
        icons.add("arrow alternate circle right");
        icons.add("dolly flatbed");
        icons.add("bell outline");
        icons.add("pinterest");
        icons.add("list alternate outline");
        icons.add("object ungroup outline");
        icons.add("app store");
        icons.add("themeisle");
        icons.add("bell slash");
        icons.add("tripadvisor");
        icons.add("itunes note");
        icons.add("envelope square");
        icons.add("mars stroke");
        icons.add("imdb");
        icons.add("window restore outline");
        icons.add("shopping cart");
        icons.add("hand point left");
        icons.add("microphone");
        icons.add("female");
        icons.add("help link");
        icons.add("cloudsmith");
        icons.add("deviantart");
        icons.add("paw");
        icons.add("hourglass start");
        icons.add("chevron circle left");
        icons.add("adn");
        icons.add("hand paper outline");
        icons.add("check");
        icons.add("list");
        icons.add("connectdevelop");
        icons.add("users");
        icons.add("quidditch");
        icons.add("pagelines");
        icons.add("calendar plus");
        icons.add("neuter");
        icons.add("user circle");
        icons.add("exchange");
        icons.add("file pdf outline");
        icons.add("child");
        icons.add("vnv");
        icons.add("gratipay");
        icons.add("cpanel");
        icons.add("sellsy");
        icons.add("long arrow alternate down");
        icons.add("paste");
        icons.add("medium");
        icons.add("keyboard outline");
        icons.add("camera retro");
        icons.add("undo");
        icons.add("dribbble square");
        icons.add("medium m");
        icons.add("strikethrough");
        icons.add("umbrella");
        icons.add("notched circle loading");
        icons.add("mobile");
        icons.add("magnet");
        icons.add("mars double");
        icons.add("vimeo");
        icons.add("coffee");
        icons.add("clone");
        icons.add("avianex");
        icons.add("check square outline");
        icons.add("pushed");
        icons.add("ticket alternate");
        icons.add("arrow alternate circle left");
        icons.add("gift");
        icons.add("copyright");
        icons.add("lock open");
        icons.add("stack overflow");
        icons.add("th list");
        icons.add("eject");
        icons.add("linkedin in");
        icons.add("slack");
        icons.add("copy outline");
        icons.add("sass");
        icons.add("play circle");
        icons.add("user secret");
        icons.add("balance scale");
        icons.add("angular");
        icons.add("cc amazon pay");
        icons.add("behance");
        icons.add("snapchat square");
        icons.add("h square");
        icons.add("newspaper outline");
        icons.add("file word");
        icons.add("star half");
        icons.add("caret square up outline");
        icons.add("superpowers");
        icons.add("save");
        icons.add("cc apple pay");
        icons.add("caret square right");
        icons.add("quote right");
        icons.add("clipboard check");
        icons.add("mouse pointer");
        icons.add("share");
        icons.add("transgender alternate");
        icons.add("gavel");
        icons.add("uikit");
        icons.add("sort numeric up");
        icons.add("snowflake");
        icons.add("retweet");
        icons.add("question");
        icons.add("hotjar");
        icons.add("heading");
        icons.add("hand point up");
        icons.add("phabricator");
        icons.add("align justify");
        icons.add("itunes");
        icons.add("bowling ball");
        icons.add("kickstarter k");
        icons.add("anchor");
        icons.add("php");
        icons.add("cog");
        icons.add("object group");
        icons.add("hdd outline");
        icons.add("sun outline");
        icons.add("modx");
        icons.add("list alternate");
        icons.add("battery three quarters");
        icons.add("small home");
        icons.add("arrow circle right");
        icons.add("folder outline");
        icons.add("external alternate");
        icons.add("home");
        icons.add("pound sign");
        icons.add("object group outline");
        icons.add("file audio outline");
        icons.add("sitemap");
        icons.add("arrow up");
        icons.add("chevron up");
        icons.add("window maximize");
        icons.add("subscript");
        icons.add("yelp");
        icons.add("ellipsis vertical");
        icons.add("pinterest p");
        icons.add("clock");
        icons.add("less");
        icons.add("black user");
        icons.add("rupee sign");
        icons.add("golf ball");
        icons.add("street view");
        icons.add("sign in alternate");
        icons.add("chess knight");
        icons.add("credit card");
        icons.add("hubspot");
        icons.add("google plus square");
        icons.add("map signs");
        icons.add("indent");
        icons.add("university");
        icons.add("redo");
        icons.add("text height");
        icons.add("universal access");
        icons.add("grunt");
        icons.add("times");
        icons.add("page4");
        icons.add("mizuni");
        icons.add("alternate github");
        icons.add("rendact");
        icons.add("clipboard list");
        icons.add("contao");
        icons.add("magento");
        icons.add("comments");
        icons.add("user circle outline");
        icons.add("chess queen");
        icons.add("fort awesome");
        icons.add("id card");
        icons.add("comment outline");
        icons.add("apple pay");
        icons.add("flickr");
        icons.add("searchengin");
        icons.add("tty");
        icons.add("joget");
        icons.add("top right corner add");
        icons.add("phoenix framework");
        icons.add("toggle on");
        icons.add("cc discover");
        icons.add("ship");
        icons.add("stumbleupon circle");
        icons.add("hand peace");
        icons.add("recycle");
        icons.add("align left");
        icons.add("shirtsinbulk");
        icons.add("angle double down");
        icons.add("save outline");
        icons.add("reddit square");
        icons.add("ioxhost");
        icons.add("file excel outline");
        icons.add("js square");
        icons.add("file powerpoint outline");
        icons.add("cc stripe");
        icons.add("hooli");
        icons.add("warehouse");
        icons.add("sort down");
        icons.add("eye");
        icons.add("git square");
        icons.add("medapps");
        icons.add("envelope");
        icons.add("medkit");
        icons.add("internet explorer");
        icons.add("check circle");
        icons.add("algolia");
        icons.add("long arrow alternate up");
        icons.add("supple");
        icons.add("circle");
        icons.add("flask");
        icons.add("plus circle");
        icons.add("cloud upload");
        icons.add("cut");
        icons.add("list ol");
        icons.add("hand lizard");
        icons.add("fonticons fi");
        icons.add("comments outline");
        icons.add("viber");
        icons.add("grav");
        icons.add("rss");
        icons.add("app store ios");
        icons.add("weibo");
        icons.add("smile outline");
        icons.add("facebook messenger");
        icons.add("trophy");
        icons.add("stethoscope");
        icons.add("paypal");
        icons.add("firstdraft");
        icons.add("file alternate");
        icons.add("hashtag");
        icons.add("bottom right corner add");
        icons.add("code branch");
        icons.add("bell slash outline");
        icons.add("binoculars");
        icons.add("ellipsis horizontal");
        icons.add("xbox");
        icons.add("info circle");
        icons.add("circle notch");
        icons.add("cc amex");
        icons.add("arrows alternate");
        icons.add("servicestack");
        icons.add("cloudscale");
        icons.add("window minimize outline");
        icons.add("microphone slash");
        icons.add("superscript");
        icons.add("thermometer quarter");
        icons.add("centercode");
        icons.add("bomb");
        icons.add("bitbucket");
        icons.add("microsoft");
        icons.add("life ring");
        icons.add("star half outline");
        icons.add("stripe");
        icons.add("meetup");
        icons.add("spinner");
        icons.add("hand lizard outline");
        icons.add("chess");
        icons.add("unlock");
        icons.add("goodreads g");
        icons.add("chess bishop");
        icons.add("weight");
        icons.add("free code camp");
        icons.add("eye slash");
        icons.add("bold");
        icons.add("sync");
        icons.add("delicious");
        icons.add("patreon");
        icons.add("futbol");
        icons.add("bookmark");
        icons.add("cc paypal");
        icons.add("file image outline");
        icons.add("weixin");
        icons.add("check circle outline");
        icons.add("expeditedssl");
        icons.add("linode");
        icons.add("bolt");
        icons.add("500px");
        icons.add("sort amount down");
        icons.add("crop");
        icons.add("dot circle outline");
        icons.add("first order");
        icons.add("whatsapp");
        icons.add("tencent weibo");
        icons.add("blind");
        icons.add("th");
        icons.add("bluetooth b");
        icons.add("gg circle");
        icons.add("book");
        icons.add("truck");
        icons.add("android");
        icons.add("chess board");
        icons.add("blogger");
        icons.add("erlang");
        icons.add("video");
        icons.add("slideshare");
        icons.add("bullhorn");
        icons.add("tint");
        icons.add("gitkraken");
        icons.add("gitter");
        icons.add("accusoft");
        icons.add("leanpub");
        icons.add("playstation");
        icons.add("staylinked");
        icons.add("trademark");
        icons.add("object ungroup");
        icons.add("level down alternate");
        icons.add("whatsapp square");
        icons.add("vimeo v");
        icons.add("cloud download");
        icons.add("thermometer empty");
        icons.add("resolving");
        icons.add("frown");
        icons.add("hdd");
        icons.add("thermometer three quarters");
        icons.add("npm");
        icons.add("file video outline");
        icons.add("volleyball ball");
        icons.add("tachometer alternate");
        icons.add("windows");
        icons.add("hand pointer");
        icons.add("discourse");
        icons.add("hand peace outline");
        icons.add("replyd");
        icons.add("aws");
        icons.add("arrow alternate circle left outline");
        icons.add("play");
        icons.add("rebel");
        icons.add("ruble sign");
        icons.add("heartbeat");
        icons.add("file word outline");
        icons.add("pallet");
        icons.add("thermometer full");
        icons.add("sort up");
        icons.add("check");
        icons.add("google plus g");
        icons.add("ns8");
        icons.add("twitter");
        icons.add("hockey puck");
        icons.add("github alternate");
        icons.add("fax");
        icons.add("sticky note");
        icons.add("images");
        icons.add("phone square");
        icons.add("paper plane outline");
        icons.add("ember");
        icons.add("hand point left outline");
        icons.add("openid");
        icons.add("lyft");
        icons.add("ambulance");
        icons.add("gripfire");
        icons.add("arrow down");
        icons.add("moon");
        icons.add("share square");
        icons.add("phone");
        icons.add("gamepad");
        icons.add("youtube square");
        icons.add("dolly");
        icons.add("tumblr");
        icons.add("microchip");
        icons.add("buromobelexperte");
        icons.add("window minimize");
        icons.add("pen square");
        icons.add("vaadin");
        icons.add("caret square up");
        icons.add("optin monster");
        icons.add("font awesome flag");
        icons.add("hand scissors");
        icons.add("braille");
        icons.add("calendar check");
        icons.add("trash alternate");
        icons.add("file audio");
        icons.add("twitch");
        icons.add("frown outline");
        icons.add("hand point right");
        icons.add("lightbulb outline");
        icons.add("shipping fast");
        icons.add("ravelry");
        icons.add("angle left");
        icons.add("thumbs up");
        icons.add("forumbee");
        icons.add("etsy");
        icons.add("red dont");
        icons.add("trash alternate outline");
        icons.add("aviato");
        icons.add("address card outline");
        icons.add("battery full");
        icons.add("fort awesome alternate");
        icons.add("crosshairs");
        icons.add("long arrow alternate right");
        icons.add("mini home");
        icons.add("d and d");
        icons.add("italic");
        icons.add("list ul");
        icons.add("shopping basket");
        icons.add("font awesome alternate");
        icons.add("cloud");
        icons.add("dashcube");
        icons.add("audible");
        icons.add("pied piper pp");
        icons.add("snowflake outline");
        icons.add("calendar outline");
        icons.add("vine");
        icons.add("sync alternate");
        icons.add("rss square");
        icons.add("check square");
        icons.add("tree");
        icons.add("archive");
        icons.add("history");
        icons.add("gofore");
        icons.add("pause");
        icons.add("square");
        icons.add("google plus");
        icons.add("black tie");
        icons.add("envelope outline");
        icons.add("caret up");
        icons.add("foursquare");
        icons.add("space shuttle");
        icons.add("uber");
        icons.add("percent");
        icons.add("angellist");
        icons.add("korvue");
        icons.add("database");
        icons.add("behance square");
        icons.add("arrow alternate circle up");
        icons.add("address book");
        icons.add("genderless");
        icons.add("hospital");
        icons.add("hand rock");
        icons.add("terminal");
        icons.add("fighter jet");
        icons.add("cc visa");
        icons.add("yandex");
        icons.add("folder open outline");
        icons.add("cc diners club");
        icons.add("caret square down");
        icons.add("lightbulb");
        icons.add("vuejs");
        icons.add("hand paper");
        icons.add("address card");
        icons.add("user");
        icons.add("american sign language interpreting");
        icons.add("plane");
        icons.add("chart line");
        icons.add("chess pawn");
        icons.add("underline");
        icons.add("lastfm");
        icons.add("asterisk");
        icons.add("step backward");
        icons.add("digg");
        icons.add("ban");
        icons.add("hourglass");
        icons.add("top left corner add");
        icons.add("goodreads");
        icons.add("stop circle outline");
        icons.add("cube");
        icons.add("arrow circle left");
        icons.add("beer");
        icons.add("thumbs down outline");
        icons.add("dyalog");
        icons.add("angle right");
        icons.add("puzzle piece");
        icons.add("angle up");
        icons.add("mobile alternate");
        icons.add("tags");
        icons.add("money bill alternate");
        icons.add("globe");
        icons.add("at");
        icons.add("low vision");
        icons.add("unlock alternate");
        icons.add("freebsd");
        icons.add("search minus");
        icons.add("tiny home");
        icons.add("undo alternate");
        icons.add("deaf");
        icons.add("glide g");
        icons.add("cuttlefish");
        icons.add("search");
        icons.add("id badge outline");
        icons.add("dochub");
        icons.add("xing");
        icons.add("pied piper");
        icons.add("xing square");
        icons.add("window close");
        icons.add("shield alternate");
        icons.add("wikipedia w");
        icons.add("long arrow alternate left");
        icons.add("calendar minus outline");
        icons.add("thumbs down");
        icons.add("envelope open");
        icons.add("facebook square");
        icons.add("fly");
        icons.add("vimeo square");
        icons.add("hourglass half");
        icons.add("user outline");
        icons.add("stopwatch");
        icons.add("steam symbol");
        icons.add("font");
        icons.add("bed");
        icons.add("close link");
        icons.add("star outline");
        icons.add("times circle");
        icons.add("language");
        icons.add("heart outline");
        icons.add("chart bar outline");
        icons.add("ethereum");
        icons.add("caret square left outline");
        icons.add("fonticons");
        icons.add("file excel");
        icons.add("images outline");
        icons.add("redo alternate");
        icons.add("reply");
        icons.add("barcode");
        icons.add("signal");
        icons.add("level up alternate");
        icons.add("info");
        icons.add("train");
        icons.add("image");
        icons.add("black users");
        icons.add("file video");
        icons.add("chrome");
        icons.add("spinner loading");
        icons.add("counterclockwise rotated cloud");
        icons.add("shopping bag");
        icons.add("shekel sign");
        icons.add("accessible icon");
        icons.add("drupal");
        icons.add("adjust");
        icons.add("arrow circle up");
        icons.add("arrows alternate vertical");
        icons.add("address book outline");
        icons.add("caret right");
        icons.add("qrcode");
        icons.add("assistive listening systems");
        icons.add("telegram plane");
        icons.add("content");
        icons.add("random");
        icons.add("lemon");
        icons.add("arrow left");
        icons.add("desktop");
        icons.add("venus");
        icons.add("podcast");
        icons.add("product hunt");
        icons.add("gg");
        icons.add("window restore");
        icons.add("home");
        icons.add("circle outline");
        icons.add("print");
        icons.add("blackberry");
        icons.add("calendar check outline");
        icons.add("hospital symbol");
        icons.add("cc jcb");
        icons.add("codiepie");
        icons.add("basketball ball");
        icons.add("bitcoin");
        icons.add("github square");
        icons.add("hand point right outline");
        icons.add("clipboard");
        icons.add("external square alternate");
        icons.add("tablet");
        icons.add("map pin");
        icons.add("dna");
        icons.add("lock");
        icons.add("table tennis");
        icons.add("tasks");
        icons.add("chess king");
        icons.add("tablet alternate");
        icons.add("bity");
        icons.add("python");
        icons.add("closed captioning");
        icons.add("map marker alternate");
        icons.add("id badge");
        icons.add("lemon outline");
        icons.add("bell");
        icons.add("keycdn");
        icons.add("user md");
        icons.add("thumbtack");
        icons.add("utensils");
        icons.add("window maximize outline");
        icons.add("server");
        icons.add("paragraph");
        icons.add("file powerpoint");
        icons.add("cc mastercard");
        icons.add("eye slash outline");
        icons.add("elementor");
        icons.add("stop circle");
        icons.add("js");
        icons.add("comment alternate");
        icons.add("soundcloud");
        icons.add("viacoin");
        icons.add("bimobject");
        icons.add("sticker mule");
        icons.add("world");
        icons.add("file");
        icons.add("i cursor");
        icons.add("jenkins");
        icons.add("pied piper alternate");
        icons.add("flag outline");
        icons.add("audio description");
        icons.add("camera");
        icons.add("map");
        icons.add("table");
        icons.add("chart area");
        icons.add("reply all");
        icons.add("get pocket");
        icons.add("forward");
        icons.add("wordpress simple");
        icons.add("user times");
        icons.add("jsfiddle");
        icons.add("search plus");
        icons.add("yoast");
        icons.add("birthday cake");
        icons.add("sticky note outline");
        icons.add("hacker news");
        icons.add("opera");
        icons.add("mail");
        icons.add("file code outline");
        icons.add("usb");
        icons.add("chevron down");
        icons.add("rockrms");
        icons.add("plus square");
        icons.add("nintendo switch");
        icons.add("sort alphabet down");
        icons.add("snapchat ghost");
        icons.add("smile");
        icons.add("closed captioning outline");
        icons.add("chevron circle right");
        icons.add("download");
        icons.add("edge");
        icons.add("autoprefixer");
        icons.add("graduation cap");
        icons.add("opencart");
        icons.add("backward");
        icons.add("folder open");
        icons.add("power off");
        icons.add("venus mars");
        icons.add("corner add");
        icons.add("palfed");
        icons.add("angle double right");
        icons.add("mars stroke horizontal");
        icons.add("share square outline");
        icons.add("joomla");
        icons.add("sort");
        icons.add("film");
        icons.add("thermometer half");
        icons.add("heart");
        icons.add("meh outline");
        icons.add("plus");
        icons.add("trash");
        icons.add("wrench");
        icons.add("amazon");
        icons.add("expand");
        icons.add("volume down");
        icons.add("compass");
        icons.add("rocketchat");
        icons.add("image outline");
        icons.add("exchange alternate");
        icons.add("plug");
        icons.add("gitlab");
        icons.add("thermometer");
        icons.add("deskpro");
        icons.add("nutritionix");
        icons.add("calendar plus outline");
        icons.add("kickstarter");
        icons.add("baseball ball");
        icons.add("cloudversify");
        icons.add("simplybuilt");
        icons.add("euro sign");
        icons.add("code");
        icons.add("certificate");
        icons.add("yen sign");
        icons.add("box");
        icons.add("google drive");
        icons.add("sign out alternate");
        icons.add("exclamation circle");
        icons.add("meh");
        icons.add("syringe");
        icons.add("envelope open outline");
        icons.add("caret left");
        icons.add("chevron circle down");
        icons.add("quinscape");
        icons.add("battery half");
        icons.add("calendar alternate");
        icons.add("circular users");
        icons.add("keyboard");
        icons.add("houzz");
        icons.add("hand point down");
        icons.add("horizontally flipped cloud");
        icons.add("edit outline");
        icons.add("chevron right");
        icons.add("puzzle");
        icons.add("window close outline");
        icons.add("eye dropper");
        icons.add("file archive");
        icons.add("pause circle");
        icons.add("amazon pay");
        icons.add("rocket");
        icons.add("registered");
        icons.add("file image");
        icons.add("instagram");
        icons.add("venus double");
        icons.add("hacker news square");
        icons.add("viadeo");
        icons.add("id card outline");
        icons.add("reddit");
        icons.add("fire");
        icons.add("boxes");
        icons.add("node js");
        icons.add("qq");
        icons.add("stack exchange");
        icons.add("github");
        icons.add("edit");
        icons.add("phone volume");
        icons.add("expand arrows alternate");
        icons.add("taxi");
        icons.add("facebook f");
        icons.add("redriver");
        icons.add("minus square");
        icons.add("uniregistry");
        icons.add("asterisk loading");
        icons.add("node");
        icons.add("asymmetrik");
        icons.add("arrow alternate circle down outline");
        icons.add("male");
        icons.add("minus");
        icons.add("shower");
        icons.add("safari");
        icons.add("tumblr square");
        icons.add("y combinator");
        icons.add("industry");
        icons.add("linkify");
        icons.add("sun");
        icons.add("step forward");
        icons.add("btc");
        icons.add("money bill alternate outline");
        icons.add("mix");
        icons.add("key");
        icons.add("football ball");
        icons.add("adversal");
        icons.add("calendar");
        icons.add("schlix");
        icons.add("skyatlas");
        icons.add("star");
        icons.add("spotify");
        icons.add("dollar sign");
        icons.add("renren");
        icons.add("draft2digital");
        icons.add("hand point down outline");
        icons.add("subway");
        icons.add("th");
        icons.add("bug");
        icons.add("cogs");
        icons.add("question circle outline");
        icons.add("glide");
        icons.add("hand scissors outline");
        icons.add("digital ocean");
        icons.add("times circle outline");
        icons.add("bus");
        icons.add("tv");
        icons.add("file archive outline");
        icons.add("odnoklassniki square");
        icons.add("wordpress");
        icons.add("headphones");
        icons.add("sort amount up");
        icons.add("exclamation");
        icons.add("calendar minus");
        icons.add("motorcycle");
        icons.add("text width");
        icons.add("hand rock outline");
        icons.add("glass martini");
        icons.add("yahoo");
        icons.add("blogger b");
        icons.add("monero");
        icons.add("file alternate outline");
        icons.add("transgender");
        icons.add("share alternate");
        icons.add("untappd");
        icons.add("arrow alternate circle down");
        icons.add("fire extinguisher");
        icons.add("suitcase");
        icons.add("caret square down outline");
        icons.add("battery empty");
        icons.add("arrow alternate circle up outline");
        icons.add("circle outline");
        icons.add("discord");
        icons.add("dropbox");
        icons.add("vk");
        icons.add("hand pointer outline");
        icons.add("fast backward");
        icons.add("css3");
        icons.add("battery quarter");
        icons.add("wheelchair");
        icons.add("gulp");
        icons.add("angle double left");
        icons.add("sort numeric down");
        icons.add("lira sign");
        icons.add("docker");
        icons.add("handshake outline");
        icons.add("apple");
        icons.add("snapchat");
        icons.add("envira");
        icons.add("yandex international");
        icons.add("linux");
        icons.add("question circle");
        icons.add("bath");
        icons.add("mixcloud");
        icons.add("calendar alternate outline");
        icons.add("cart arrow down");
        icons.add("bandcamp");
        icons.add("stumbleupon");
        icons.add("mars");
        icons.add("scribd");
        icons.add("bars");
        icons.add("affiliatetheme");
        icons.add("sign language");
        icons.add("wpbeginner");
        icons.add("arrow alternate circle right outline");
        icons.add("studiovinari");
        icons.add("hips");
        icons.add("chart bar");
        icons.add("sellcast");
        icons.add("comment");
        icons.add("wpexplorer");
        icons.add("wpforms");
        icons.add("futbol outline");
        icons.add("osi");
        icons.add("apper");
        icons.add("square outline");
        icons.add("earlybirds");
        icons.add("react");
        icons.add("road");
        icons.add("periscope");
        icons.add("paperclip");
        icons.add("deploydog");
        icons.add("calculator");
        icons.add("medrt");
        icons.add("wifi");
        icons.add("compress");
        icons.add("registered outline");
        icons.add("font awesome");
        icons.add("hand spock");
        icons.add("caret square right outline");
        icons.add("hire a helper");
        icons.add("hand point up outline");
        icons.add("credit card outline");
        icons.add("creative commons");
        icons.add("flag");
        icons.add("columns");
        icons.add("play circle outline");
        icons.add("gem");
        icons.add("ussunnah");
        icons.add("mars stroke vertical");
        icons.add("viadeo square");
        icons.add("file outline");
        icons.add("caret square left");
        icons.add("copy");
        icons.add("dribbble");
        icons.add("moon outline");
        icons.add("plus square outline");
        icons.add("vertically flipped cloud");
        icons.add("facebook");
        icons.add("leaf");
        icons.add("filter");
        icons.add("map marker");
        icons.add("calendar times outline");
        icons.add("cubes");
        icons.add("comment alternate outline");
        icons.add("hand spock outline");
        icons.add("inbox");
        icons.add("align center");
        icons.add("magic");
        icons.add("trello");
        icons.add("arrow circle down");
        icons.add("firefox");
        icons.add("fitted help");
        icons.add("angle down");
        icons.add("twitter square");
        icons.add("flag checkered");
        icons.add("whmcs");
        icons.add("disabled warning sign");
        icons.add("tag");
        icons.add("stripe s");
        icons.add("dot circle");
        icons.add("minus circle");
        icons.add("cart plus");
        icons.add("laravel");
        icons.add("briefcase");
        icons.add("minus square outline");
        icons.add("linechat");
        icons.add("sort alphabet up");
        icons.add("bullseye");
        icons.add("sistrix");
        icons.add("odnoklassniki");
        icons.add("hourglass outline");
        icons.add("napster");
        icons.add("toggle off");
        icons.add("chevron circle up");
        icons.add("youtube");
        icons.add("handshake");
        icons.add("paint brush");
        icons.add("mercury");
        icons.add("maxcdn");
        icons.add("speakap");
        icons.add("linkedin");
        icons.add("clock outline");
        icons.add("pause circle outline");
        icons.add("git");
        icons.add("pencil alternate");
        icons.add("arrow right");
        icons.add("car");
        icons.add("css3 alternate");
        icons.add("steam");
        icons.add("angrycreative");
        icons.add("quote left");
        icons.add("copyright outline");
        icons.add("hospital outline");
        icons.add("telegram");
        icons.add("paper plane");
        icons.add("bookmark outline");
        icons.add("flipboard");
        icons.add("lastfm square");
        icons.add("unlink");
        icons.add("bluetooth");
        icons.add("typo3");
        icons.add("clone outline");
        icons.add("life ring outline");
        icons.add("sliders horizontal");
        icons.add("pinterest square");
        return new ArrayList<>(icons);
    }
}
