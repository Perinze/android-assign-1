package com.perinze.demo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.perinze.demo.databinding.FragmentFirstBinding
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getHtmlFromWeb()
        //binding.buttonFirst.setOnClickListener {
        //    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        //}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getHtmlFromWeb() {
        val card = listOf(
            binding.card0,
            binding.card1,
            binding.card2,
            binding.card3,
            binding.card4
        )
        Thread {
            val stringBuilder = java.lang.StringBuilder()
            val subtitleList: ArrayList<Triple<String, String, String>> = ArrayList()
            try {
                val doc: Document = Jsoup.connect("http://www.gov.cn/zhuanti/2023qglh/index.htm").get()
                val images: Elements = doc.select("div[class=pannel-image]")
                val subtitles: Elements = doc.select("div[class=subtitle]")
                // class="item slidesjs-slide"
                Log.d("slides", images.toString())
                Log.d("slides", subtitles.toString())
                for (i in 0 until images.size) {
                    val image = images[i].select("img")
                    val link = subtitles[i].select("a[href]")
                    Log.d("img", image.toString())
                    Log.d("link", link.toString())
                    subtitleList.add(Triple(link.text(), link.attr("href"), image.attr("src")))
                }
            } catch (e: IOException) {
                stringBuilder.append("Error: ").append(e.message).append("\n")
            }
            activity?.runOnUiThread {
                for (i in 0..4) {
                    val imageView = card[i].getChildAt(0) as ImageView
                    val imageSrc = "http://www.gov.cn" + subtitleList[i].third
                    Log.d("img", imageSrc)
                    imageView.load(imageSrc)
                    val textView = card[i].getChildAt(1) as TextView
                    textView.text = subtitleList[i].first
                    card[i].setOnClickListener { it ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(subtitleList[i].second))
                        startActivity(intent)
                    }
                }
            }
        }.start()
    }
}