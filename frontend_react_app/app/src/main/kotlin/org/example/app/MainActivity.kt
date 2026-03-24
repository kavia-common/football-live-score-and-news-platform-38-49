package org.example.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import org.example.app.network.BackendApi
import org.example.app.realtime.RealtimeUpdatesManager

class MainActivity : Activity() {

    private lateinit var sessionManager: SessionManager
    private val api = BackendApi()

    private lateinit var titleText: TextView
    private lateinit var sectionTitle: TextView
    private lateinit var statusText: TextView
    private lateinit var listView: ListView

    private lateinit var navHome: Button
    private lateinit var navMatches: Button
    private lateinit var navNews: Button
    private lateinit var logoutButton: Button

    private var currentTab: Tab = Tab.HOME

    private var realtimeManager: RealtimeUpdatesManager? = null

    private enum class Tab { HOME, MATCHES, NEWS }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        // Guard: require session.
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        titleText = findViewById(R.id.titleText)
        sectionTitle = findViewById(R.id.sectionTitle)
        statusText = findViewById(R.id.statusText)
        listView = findViewById(R.id.listView)

        navHome = findViewById(R.id.navHome)
        navMatches = findViewById(R.id.navMatches)
        navNews = findViewById(R.id.navNews)
        logoutButton = findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener {
            realtimeManager?.stop()
            sessionManager.clear()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        navHome.setOnClickListener { switchTab(Tab.HOME) }
        navMatches.setOnClickListener { switchTab(Tab.MATCHES) }
        navNews.setOnClickListener { switchTab(Tab.NEWS) }

        // Health check (non-blocking UX; we just show status).
        val health = api.healthCheck()
        statusText.text = if (health.isSuccess) "Connected" else "Offline mode (mock data)"

        // Default tab
        switchTab(Tab.HOME)
    }

    override fun onStart() {
        super.onStart()
        // Start real-time updates for HOME tab live list (polling).
        // We keep it running; UI just changes list content.
        if (realtimeManager == null) {
            realtimeManager = RealtimeUpdatesManager(
                api = api,
                onLiveMatchesUpdate = { matches ->
                    if (currentTab == Tab.HOME) {
                        setList(matches)
                        statusText.text = "Live updated"
                    }
                },
                onStatus = { status ->
                    statusText.text = status
                }
            )
        }
        realtimeManager?.start(sessionManager.getAuthToken())
    }

    override fun onStop() {
        super.onStop()
        realtimeManager?.stop()
    }

    private fun switchTab(tab: Tab) {
        currentTab = tab
        updateNavStyles()

        when (tab) {
            Tab.HOME -> {
                titleText.text = "Home"
                sectionTitle.text = getString(R.string.section_live_now)
                statusText.text = getString(R.string.loading)

                val res = api.getLiveMatches(sessionManager.getAuthToken())
                if (res.isSuccess) {
                    setList(res.getOrDefault(emptyList()))
                    statusText.text = "Showing live scores"
                } else {
                    statusText.text = res.exceptionOrNull()?.message ?: getString(R.string.error_generic)
                }
            }

            Tab.MATCHES -> {
                titleText.text = "Matches"
                sectionTitle.text = getString(R.string.section_upcoming)
                statusText.text = getString(R.string.loading)

                val res = api.getUpcomingMatches(sessionManager.getAuthToken())
                if (res.isSuccess) {
                    setList(res.getOrDefault(emptyList()))
                    statusText.text = "Showing schedule"
                } else {
                    statusText.text = res.exceptionOrNull()?.message ?: getString(R.string.error_generic)
                }
            }

            Tab.NEWS -> {
                titleText.text = "News"
                sectionTitle.text = getString(R.string.section_latest_news)
                statusText.text = getString(R.string.loading)

                val res = api.getNews(sessionManager.getAuthToken())
                if (res.isSuccess) {
                    setList(res.getOrDefault(emptyList()))
                    statusText.text = "Showing latest news"
                } else {
                    statusText.text = res.exceptionOrNull()?.message ?: getString(R.string.error_generic)
                }
            }
        }
    }

    private fun setList(items: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter
    }

    private fun updateNavStyles() {
        // Primary: #2563EB; neutral: #e5e7eb
        fun setActive(btn: Button, active: Boolean) {
            if (active) {
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF2563EB.toInt()))
                btn.setTextColor(0xFFFFFFFF.toInt())
            } else {
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFE5E7EB.toInt()))
                btn.setTextColor(0xFF111827.toInt())
            }
        }

        setActive(navHome, currentTab == Tab.HOME)
        setActive(navMatches, currentTab == Tab.MATCHES)
        setActive(navNews, currentTab == Tab.NEWS)
    }
}
