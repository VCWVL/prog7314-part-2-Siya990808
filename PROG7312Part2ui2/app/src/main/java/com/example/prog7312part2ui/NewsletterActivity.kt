package com.example.prog7312part2ui

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class NewsletterActivity : AppCompatActivity() {

    private lateinit var articleImage: ImageView
    private lateinit var articleContent: TextView
    private lateinit var previousBtn: FrameLayout
    private lateinit var nextBtn: FrameLayout

    // You can replace this with your own image URL
    private val imageUrl = "https://example.com/your-study-image.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newsletter)

        // Initialize views
        articleImage = findViewById(R.id.articleImage)
        articleContent = findViewById(R.id.articleContent)
        previousBtn = findViewById(R.id.previousBtn)
        nextBtn = findViewById(R.id.nextBtn)

        // Load article content
        loadArticle()

        // Load image from URL (optional - requires Glide library)
        Glide.with(this).load(imageUrl).into(articleImage)
        // Or set a local drawable:
        // articleImage.setImageResource(R.drawable.your_image)

        // Button listeners
        previousBtn.setOnClickListener {
            Toast.makeText(this, "Previous article", Toast.LENGTH_SHORT).show()
            // Navigate to previous article
            // You can implement navigation logic here
        }

        nextBtn.setOnClickListener {
            Toast.makeText(this, "Next article", Toast.LENGTH_SHORT).show()
            // Navigate to next article
            // You can implement navigation logic here
        }
    }

    private fun loadArticle() {
        val article = """
            University life can feel like a constant juggling act. Between assignments, lectures, part-time jobs, and the ever-present need to socialize, the key is learning how to create balance rather than burning out. Something at stake.
            
            Something as simple as setting aside 20 minutes each day to review notes or break down a big project can prevent panic later. It's not about doing everything perfectly, but about staying engaged without sacrificing mental health.
            
            At the same time, remember that rest isn't wasted time. Taking a break, going for a walk with a friend, having a simple meal, or even scrolling guilt-free for a while can recharge your batteries in ways that an endless study session can't.
            
            Think of university not just as a place to earn a degree, but as a testing grounds for managing your time, energy, and productivity. You only care so much about graduating.
            
            And don't forget—no one has it all figured out. If you're struggling, chances are the student sitting next to you is too. Reach out, share tips, swap notes, or just talk. It isn't just about making connections as it is about academics, and leaning on your peers can make navigating a little less daunting and a lot more fun.
            
            Apps like StudyHive are designed to support this balance. By organizing your classes, tracking assignments, and keeping important dates visible, tools like these reduce mental clutter and help you focus on what actually matters—learning, growing, and enjoying the journey.
            
            Remember: productivity isn't about doing more; it's about doing what matters, efficiently and sustainably. Use the tools available, lean on your support network, and give yourself permission to rest. That's how you thrive, not just survive, in student life.
        """.trimIndent()

        articleContent.text = article
    }
}