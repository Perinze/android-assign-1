package com.perinze.demo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perinze.demo.databinding.FragmentSecondBinding
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.lang.StringBuilder

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getHtmlFromWeb()

        recyclerView = binding.recyclerView

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView?.layoutManager = linearLayoutManager

        val adapter = LinkAdapter(activity!!, listOf())
        recyclerView?.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getHtmlFromWeb() {
        Thread {
            val list = ArrayList<Pair<String, String>>()
            try {
                val doc: Document =
                    Jsoup.connect("http://www.gov.cn/zhuanti/2023qglh/index.htm").get()
                val links: Elements = doc.select("a[href]")
                for (link in links) {
                    if (link.text() == "") continue
                    var url = link.attr("href")
                    url = (if (url.startsWith("/")) "http://www.gov.cn" else "") + url
                    if (!URLUtil.isValidUrl(url)) continue
                    Log.d("name", link.text())
                    Log.d("url", url)
                    list.add(Pair(link.text(), url))
                }
            } catch (e: IOException) {
                Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
            }
            activity?.runOnUiThread {
                recyclerView?.swapAdapter(LinkAdapter(activity!!, list), false)
            }
        }.start()
    }
}