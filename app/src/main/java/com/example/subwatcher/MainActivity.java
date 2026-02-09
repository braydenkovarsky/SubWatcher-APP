package com.example.subwatcher;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent; // Needed for navigation
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<Subscription> subList;
    private SubscriptionAdapter adapter;
    private TextView tvTotalAmount, tvSubCount, tvCurrentDate;
    private Map<String, ServiceInfo> serviceDatabase;
    private String[] serviceNames;

    static class ServiceInfo {
        List<String> prices;
        String category;
        ServiceInfo(List<String> prices, String category) {
            this.prices = prices;
            this.category = category;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupMassiveData();
        createNotificationChannel();

        // UI Initialization
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvSubCount = findViewById(R.id.tvSubCount);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        RecyclerView recyclerView = findViewById(R.id.rvSubscriptions);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        LinearLayout bottomNav = findViewById(R.id.bottomNavigation);

        // Recycler Setup
        subList = new ArrayList<>();
        adapter = new SubscriptionAdapter(subList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // --- NAVIGATION CLICK LISTENERS ---
        // Index 1 is "Categories"
        bottomNav.getChildAt(1).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);
            // This line sends your current subscription list to the Categories page
            intent.putExtra("sub_list", new ArrayList<>(subList));
            startActivity(intent);
        });

        // Index 2 is "Settings"
        bottomNav.getChildAt(2).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        fabAdd.setOnClickListener(v -> showAddSubscriptionDialog());

        startClock();
        updateDashboardMetrics();
    }
    private void startClock() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String time = new SimpleDateFormat("EEEE, MMM d, yyyy / h:mm a", Locale.getDefault()).format(new Date());
                tvCurrentDate.setText(time);
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void showAddSubscriptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Subscription");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 10);

        final AutoCompleteTextView searchView = new AutoCompleteTextView(this);
        searchView.setHint("Search Service");
        searchView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, serviceNames));
        layout.addView(searchView);

        final Spinner priceSpinner = new Spinner(this);
        layout.addView(priceSpinner);

        final EditText inputCategory = new EditText(this);
        inputCategory.setHint("Category");
        layout.addView(inputCategory);

        searchView.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            ServiceInfo info = serviceDatabase.get(selected);
            if (info != null) {
                priceSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, info.prices));
                inputCategory.setText(info.category);
            }
        });

        builder.setView(layout);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = searchView.getText().toString();
            String cat = inputCategory.getText().toString();
            double price = 0.0;
            if (priceSpinner.getSelectedItem() != null) {
                try {
                    price = Double.parseDouble(priceSpinner.getSelectedItem().toString().split(" ")[0]);
                } catch (Exception e) { price = 0.0; }
            }

            if (!name.isEmpty() && price > 0) {
                subList.add(new Subscription(name, price, "Monthly", cat));
                adapter.notifyItemInserted(subList.size() - 1);
                updateDashboardMetrics();
            }
        });
        builder.show();
    }

    private void updateDashboardMetrics() {
        double total = 0;
        for (Subscription s : subList) total += s.getPrice();
        tvTotalAmount.setText(String.format("$%.2f / MONTH", total));
        tvSubCount.setText("Total Subscriptions (" + subList.size() + ")");
    }

    private void addToDB(String name, String cat, String... prices) {
        serviceDatabase.put(name, new ServiceInfo(Arrays.asList(prices), cat));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("SUB_CHANNEL", "Alerts", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private void setupMassiveData() {
        serviceDatabase = new HashMap<>();

        // --- VIDEO STREAMING ---
        addToDB("Netflix", "Streaming", "6.99 (Standard w/ Ads)", "15.49 (Standard)", "22.99 (Premium)");
        addToDB("Disney+", "Streaming", "7.99 (Basic w/ Ads)", "13.99 (Premium No Ads)", "19.99 (Duo Basic Hulu)", "24.99 (Trio Premium)");
        addToDB("Hulu", "Streaming", "7.99 (With Ads)", "17.99 (No Ads)", "76.99 (Live TV)");
        addToDB("Max (HBO)", "Streaming", "9.99 (With Ads)", "16.99 (Ad-Free)", "20.99 (Ultimate)");
        addToDB("Amazon Prime", "Streaming/Shopping", "14.99 (Monthly)", "139.00 (Yearly)", "8.99 (Video Only)");
        addToDB("Peacock", "Streaming", "5.99 (Premium)", "11.99 (Premium Plus)");
        addToDB("Paramount+", "Streaming", "5.99 (Essential)", "11.99 (with Showtime)");
        addToDB("Apple TV+", "Streaming", "9.99 (Monthly)");
        addToDB("YouTube Premium", "Streaming", "13.99 (Individual)", "22.99 (Family)", "7.99 (Student)");
        addToDB("Discovery+", "Streaming", "4.99 (Ad-Lite)", "8.99 (Ad-Free)");
        addToDB("Crunchyroll", "Streaming/Anime", "7.99 (Fan)", "9.99 (Mega Fan)", "14.99 (Ultimate Fan)");
        addToDB("Funimation", "Streaming/Anime", "5.99 (Premium)", "7.99 (Premium Plus)");
        addToDB("Starz", "Streaming", "9.99 (Monthly)", "74.99 (Yearly)");
        addToDB("Showtime", "Streaming", "10.99 (Streaming Only)");
        addToDB("MGM+", "Streaming", "6.99 (Monthly)", "58.99 (Yearly)");
        addToDB("CuriosityStream", "Education", "4.99 (Standard)", "9.99 (Smart Bundle)");
        addToDB("Nebula", "Education", "5.00 (Monthly)", "50.00 (Yearly)");
        addToDB("Twitch Turbo", "Gaming/Social", "11.99 (Monthly)");
        addToDB("Vimeo", "Productivity", "12.00 (Plus)", "20.00 (Pro)", "55.00 (Business)");
        addToDB("BritBox", "Streaming", "8.99 (Monthly)", "89.99 (Yearly)");
        addToDB("Acorn TV", "Streaming", "6.99 (Monthly)", "69.99 (Yearly)");
        addToDB("Shudder", "Streaming/Horror", "6.99 (Monthly)", "71.88 (Yearly)");
        addToDB("FuboTV", "Streaming/Sports", "79.99 (Pro)", "89.99 (Elite)", "99.99 (Premier)");
        addToDB("Sling TV", "Streaming", "40.00 (Orange)", "40.00 (Blue)", "55.00 (Orange + Blue)");
        addToDB("Philo", "Streaming", "25.00 (Monthly)");
        addToDB("ESPN+", "Sports", "10.99 (Monthly)", "109.99 (Yearly)");
        addToDB("DAZN", "Sports", "24.99 (Monthly)", "224.99 (Yearly)");
        addToDB("UFC Fight Pass", "Sports", "9.99 (Monthly)", "95.99 (Yearly)");
        addToDB("NBA League Pass", "Sports", "14.99 (Monthly)", "99.99 (Season)");
        addToDB("NFL+", "Sports", "6.99 (Monthly)", "14.99 (Premium)");
        addToDB("MLB.TV", "Sports", "29.99 (Monthly)", "149.99 (Yearly)");

        // --- MUSIC & AUDIO ---
        addToDB("Spotify", "Music", "11.99 (Premium)", "16.99 (Duo)", "19.99 (Family)", "5.99 (Student)");
        addToDB("Apple Music", "Music", "10.99 (Individual)", "16.99 (Family)", "5.99 (Student)");
        addToDB("YouTube Music", "Music", "10.99 (Individual)", "16.99 (Family)");
        addToDB("Amazon Music", "Music", "9.99 (Unlimited)", "16.99 (Family)");
        addToDB("Tidal", "Music", "10.99 (HiFi)", "19.99 (HiFi Plus)", "16.99 (Family HiFi)");
        addToDB("Deezer", "Music", "11.99 (Premium)", "19.99 (Family)");
        addToDB("Pandora", "Music", "4.99 (Plus)", "9.99 (Premium)", "14.99 (Family)");
        addToDB("SoundCloud", "Music", "4.99 (Go)", "9.99 (Go+)");
        addToDB("Qobuz", "Music", "12.99 (Studio)", "15.00 (Sublime)");
        addToDB("SiriusXM", "Audio", "13.00 (Music Only)", "18.99 (Platinum)", "23.99 (All Access)");
        addToDB("Audible", "Books", "7.95 (Plus)", "14.95 (Premium Plus 1 Credit)", "22.95 (Premium Plus 2 Credits)");
        addToDB("TuneIn", "Audio", "9.99 (Premium)");
        addToDB("iHeartRadio", "Audio", "4.99 (Plus)", "9.99 (All Access)");
        addToDB("Napster", "Music", "9.99 (Individual)", "14.99 (Family)");
        addToDB("Idagio", "Music", "9.99 (Premium)", "14.99 (Premium+)");
        addToDB("Blinkist", "Education", "14.99 (Monthly)", "89.99 (Yearly)");
        addToDB("Scribd (Everand)", "Books", "11.99 (Monthly)");

        // --- GAMING ---
        addToDB("Xbox Game Pass", "Gaming", "9.99 (Core)", "10.99 (Console)", "9.99 (PC)", "16.99 (Ultimate)");
        addToDB("PlayStation Plus", "Gaming", "9.99 (Essential)", "14.99 (Extra)", "17.99 (Premium)");
        addToDB("Nintendo Switch Online", "Gaming", "3.99 (Monthly)", "19.99 (Yearly)", "49.99 (Yearly + Expansion)");
        addToDB("EA Play", "Gaming", "4.99 (Monthly)", "29.99 (Yearly)", "14.99 (Pro Monthly)");
        addToDB("Ubisoft+", "Gaming", "14.99 (PC Access)", "17.99 (Multi Access)");
        addToDB("GeForce Now", "Gaming/Cloud", "9.99 (Priority)", "19.99 (Ultimate)");
        addToDB("Humble Choice", "Gaming", "11.99 (Monthly)");
        addToDB("Discord Nitro", "Social", "2.99 (Basic)", "9.99 (Nitro)", "99.99 (Nitro Yearly)");
        addToDB("Roblox Premium", "Gaming", "4.99 (450 Robux)", "9.99 (1000 Robux)", "19.99 (2200 Robux)");
        addToDB("Fallout 1st", "Gaming", "12.99 (Monthly)", "99.99 (Yearly)");
        addToDB("Minecraft Realms", "Gaming", "3.99 (Realms)", "7.99 (Realms Plus)");
        addToDB("Fortnite Crew", "Gaming", "11.99 (Monthly)");
        addToDB("Rockstar Games+", "Gaming", "5.99 (Monthly)");
        addToDB("Apple Arcade", "Gaming", "6.99 (Monthly)");
        addToDB("Google Play Pass", "Gaming", "4.99 (Monthly)", "29.99 (Yearly)");
        addToDB("Chess.com", "Gaming", "6.99 (Gold)", "10.99 (Platinum)", "16.99 (Diamond)");
        addToDB("Roll20", "Gaming/Tabletop", "4.17 (Plus)", "8.33 (Pro)");
        addToDB("DnD Beyond", "Gaming/Tabletop", "2.99 (Hero)", "5.99 (Master)");
        addToDB("World of Warcraft", "Gaming/MMO", "14.99 (Monthly)");
        addToDB("FFXIV Online", "Gaming/MMO", "12.99 (Entry)", "14.99 (Standard)");
        addToDB("Old School RuneScape", "Gaming/MMO", "12.49 (Monthly)", "79.99 (Yearly)");
        addToDB("EVE Online", "Gaming/MMO", "19.99 (Omega)");
        addToDB("iRacing", "Gaming/Sim", "13.00 (Monthly)");

        // --- PRODUCTIVITY & CLOUD ---
        addToDB("Microsoft 365", "Productivity", "6.99 (Personal)", "9.99 (Family)");
        addToDB("Google One", "Cloud", "1.99 (100GB)", "2.99 (200GB)", "9.99 (2TB)");
        addToDB("iCloud+", "Cloud", "0.99 (50GB)", "2.99 (200GB)", "9.99 (2TB)", "29.99 (6TB)");
        addToDB("Dropbox", "Cloud", "11.99 (Plus)", "19.99 (Professional)");
        addToDB("Adobe Creative Cloud", "Design", "59.99 (All Apps)", "22.99 (Photoshop)", "19.99 (Student)");
        addToDB("Canva", "Design", "14.99 (Pro Monthly)", "119.99 (Pro Yearly)");
        addToDB("Evernote", "Productivity", "14.99 (Personal)", "17.99 (Professional)");
        addToDB("Notion", "Productivity", "10.00 (Plus Monthly)", "8.00 (Plus Yearly)");
        addToDB("Slack", "Productivity/Work", "8.75 (Pro)", "15.00 (Business+)");
        addToDB("Zoom", "Productivity", "15.99 (Pro)", "21.99 (Business)");
        addToDB("GitHub Copilot", "Productivity/Coding", "10.00 (Individual)", "100.00 (Yearly)");
        addToDB("ChatGPT Plus", "AI", "20.00 (Plus)");
        addToDB("Claude Pro", "AI", "20.00 (Monthly)");
        addToDB("Midjourney", "AI/Design", "10.00 (Basic)", "30.00 (Standard)", "60.00 (Pro)");
        addToDB("Perplexity Pro", "AI/Search", "20.00 (Monthly)");
        addToDB("Zapier", "Productivity", "29.99 (Starter)", "73.50 (Professional)");
        addToDB("Grammarly", "Productivity", "12.00 (Premium Annual)", "30.00 (Premium Monthly)");
        addToDB("Todoist", "Productivity", "5.00 (Pro Monthly)", "48.00 (Pro Yearly)");
        addToDB("Trello", "Productivity", "6.00 (Standard)", "12.50 (Premium)");
        addToDB("Asana", "Productivity", "13.49 (Premium)", "30.49 (Business)");
        addToDB("Monday.com", "Productivity", "12.00 (Standard)", "20.00 (Pro)");
        addToDB("Airtable", "Productivity", "24.00 (Team)", "54.00 (Business)");
        addToDB("Figma", "Design", "15.00 (Pro)", "45.00 (Org)");
        addToDB("ClickUp", "Productivity", "10.00 (Unlimited)", "19.00 (Business)");
        addToDB("NordPass", "Security", "2.39 (Premium)", "3.69 (Family)");
        addToDB("Bitwarden", "Security", "10.00 (Premium Yearly)", "40.00 (Families Yearly)");

        // --- NEWS & READING ---
        addToDB("NY Times", "News", "4.00 (Basic)", "25.00 (All Access)");
        addToDB("Washington Post", "News", "4.00 (Digital)", "12.00 (Premium)");
        addToDB("Wall Street Journal", "News", "38.99 (Digital Only)");
        addToDB("The Atlantic", "News", "59.99 (Yearly Digital)");
        addToDB("Medium", "News/Writing", "5.00 (Member)", "15.00 (Friend)");
        addToDB("Substack", "News", "5.00 (Average)", "10.00 (High Tier)");
        addToDB("Kindle Unlimited", "Books", "11.99 (Monthly)");
        addToDB("Marvel Unlimited", "Books/Comics", "9.99 (Monthly)", "69.00 (Yearly)");
        addToDB("DC Universe Infinite", "Books/Comics", "7.99 (Monthly)", "74.99 (Yearly)");
        addToDB("Bloomberg", "News", "34.99 (Monthly)", "290.00 (Yearly)");
        addToDB("Financial Times", "News", "40.00 (Standard)", "69.00 (Premium)");
        addToDB("The Economist", "News", "19.90 (Digital)", "26.50 (Digital + Print)");
        addToDB("Wired", "News", "5.00 (Digital Only)", "29.99 (Yearly)");
        addToDB("New Yorker", "News", "12.00 (Monthly)", "120.00 (Yearly)");

        // --- LIFESTYLE, FITNESS & DATING ---
        addToDB("Peloton", "Fitness", "12.99 (App One)", "24.00 (App+)", "44.00 (All-Access)");
        addToDB("Duolingo", "Education", "6.99 (Super)", "9.99 (Max)");
        addToDB("Strava", "Fitness", "11.99 (Monthly)", "79.99 (Yearly)");
        addToDB("MyFitnessPal", "Fitness", "19.99 (Monthly)", "79.99 (Yearly)");
        addToDB("Fitbit Premium", "Fitness", "9.99 (Monthly)", "79.99 (Yearly)");
        addToDB("Calm", "Wellness", "14.99 (Monthly)", "69.99 (Yearly)");
        addToDB("Headspace", "Wellness", "12.99 (Monthly)", "69.99 (Yearly)");
        addToDB("MasterClass", "Education", "10.00 (Standard)", "15.00 (Plus)", "20.00 (Premium)");
        addToDB("Coursera Plus", "Education", "59.00 (Monthly)", "399.00 (Yearly)");
        addToDB("LinkedIn Premium", "Social/Work", "39.99 (Career)", "59.99 (Business)");
        addToDB("Tinder", "Dating", "24.99 (Plus)", "36.00 (Gold)", "44.99 (Platinum)");
        addToDB("Bumble", "Dating", "19.99 (Boost)", "39.99 (Premium)");
        addToDB("Hinge", "Dating", "32.99 (Hinge+)", "49.99 (HingeX)");
        addToDB("Grindr", "Dating", "19.99 (XTRA)", "39.99 (Unlimited)");
        addToDB("OkCupid", "Dating", "44.99 (Premium Monthly)");
        addToDB("Match.com", "Dating", "44.99 (Standard)");
        addToDB("Ancestry", "Lifestyle", "24.99 (US)", "39.99 (World)");
        addToDB("23andMe", "Lifestyle", "29.00 (Premium Membership)");
        addToDB("WeightWatchers", "Health", "23.00 (Core)", "45.00 (Premium)");
        addToDB("Noom", "Health", "70.00 (Monthly Auto-renew)");
        addToDB("HelloFresh", "Food", "60.00 (2 People / 2 Recipes)", "90.00 (4 People / 2 Recipes)");
        addToDB("Blue Apron", "Food", "60.95 (2 Serving)", "100.00 (4 Serving)");
        addToDB("Whoop", "Fitness", "30.00 (Monthly)", "239.00 (Yearly)");
        addToDB("Oura Ring", "Fitness", "5.99 (Membership)");

        // --- SECURITY & UTILITIES ---
        addToDB("NordVPN", "Security", "12.99 (Monthly)", "4.99 (Yearly avg)");
        addToDB("ExpressVPN", "Security", "12.95 (Monthly)", "6.67 (Yearly avg)");
        addToDB("Surfshark", "Security", "12.95 (Monthly)", "3.99 (Yearly avg)");
        addToDB("ProtonVPN", "Security", "9.99 (Monthly)", "4.99 (Yearly)");
        addToDB("Private Internet Access", "Security", "11.95 (Monthly)", "3.33 (Yearly)");
        addToDB("1Password", "Security", "2.99 (Individual)", "4.99 (Family)");
        addToDB("LastPass", "Security", "3.00 (Premium)", "4.00 (Family)");
        addToDB("Dashlane", "Security", "4.99 (Premium)", "7.49 (Friends & Family)");
        addToDB("Proton Mail", "Security", "4.99 (Mail Plus)", "12.99 (Unlimited)");
        addToDB("McAfee", "Security", "39.99 (Basic)", "119.99 (Advanced)");
        addToDB("Norton 360", "Security", "29.99 (Standard)", "49.99 (Deluxe)");
        addToDB("Malwarebytes", "Security", "3.75 (Standard)", "6.67 (Plus)");

        // --- SHOPPING & MISC ---
        addToDB("Costco", "Shopping", "60.00 (Gold Star)", "120.00 (Executive)");
        addToDB("Walmart+", "Shopping", "12.95 (Monthly)", "98.00 (Yearly)");
        addToDB("Instacart+", "Shopping", "9.99 (Monthly)", "99.00 (Yearly)");
        addToDB("DoorDash DashPass", "Food", "9.99 (Monthly)", "96.00 (Yearly)");
        addToDB("Uber One", "Food/Transit", "9.99 (Monthly)", "96.00 (Yearly)");
        addToDB("Grubhub+", "Food", "9.99 (Monthly)");
        addToDB("Lyft Pink", "Transit", "9.99 (Monthly)", "199.00 (All Access Yearly)");
        addToDB("AAA", "Service", "59.00 (Classic)", "94.00 (Plus)", "124.00 (Premier)");
        addToDB("Planet Fitness", "Fitness", "10.00 (Classic)", "24.99 (Black Card)");
        addToDB("Postmates Unlimited", "Food", "9.99 (Monthly)");
        addToDB("Shipt", "Shopping", "10.99 (Monthly)", "99.00 (Yearly)");
        addToDB("BarkBox", "Pets", "35.00 (Monthly)", "26.00 (12-Month Plan)");
        addToDB("Chewy Autoship", "Pets", "Variable (Save 5%)");
        addToDB("Dollar Shave Club", "Lifestyle", "10.00 (Starter)", "20.00 (Full Kit)");
        addToDB("Ipsy", "Lifestyle", "14.00 (Glam Bag)", "30.00 (BoxyCharm)");
        addToDB("FabFitFun", "Lifestyle", "69.99 (Seasonal)", "219.99 (Annual)");

        serviceNames = serviceDatabase.keySet().toArray(new String[0]);
        Arrays.sort(serviceNames);
    } // Closes setupMassiveData method

}