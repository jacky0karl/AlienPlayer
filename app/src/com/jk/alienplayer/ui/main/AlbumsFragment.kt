package com.jk.alienplayer.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

import com.jk.alienplayer.R
import com.jk.alienplayer.impl.MediaScanService
import com.jk.alienplayer.metadata.AlbumInfo
import com.jk.alienplayer.metadata.CurrentlistInfo
import com.jk.alienplayer.presenter.main.AlbumsPresenter
import com.jk.alienplayer.ui.adapter.AlbumsAdapter
import com.jk.alienplayer.ui.adapter.OnItemClickListener
import com.jk.alienplayer.ui.artistdetail.SongsActivity

class AlbumsFragment : Fragment() {

    private var mArtistName: String? = null
    private var mRecyclerView: RecyclerView? = null
    private var mLoading: ProgressBar? = null
    private var mAdapter: AlbumsAdapter? = null
    private var mPresenter: AlbumsPresenter? = null

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == MediaScanService.ACTION_MEDIA_SCAN_COMPLETED) {
                updateAlbums()
            }
        }
    }

    private val mOnItemClickListener = OnItemClickListener<AlbumInfo> { view, position, info ->
        if (info == null) {
            return@OnItemClickListener
        }

        val intent = Intent(activity, SongsActivity::class.java)
        intent.putExtra(SongsActivity.KEY_TYPE, CurrentlistInfo.TYPE_ALBUM)
        intent.putExtra(SongsActivity.KEY, info.id)
        intent.putExtra(SongsActivity.LABEL, info.name)
        startActivity(intent)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater!!.inflate(R.layout.fragment_albums, container, false)
        init(root)
        MediaScanService.registerScanReceiver(activity, mReceiver)
        return root
    }

    private fun init(root: View) {
        if (arguments != null) {
            mArtistName = arguments.getString(ARTIST_NAME)
        }

        mLoading = root.findViewById(R.id.loading) as ProgressBar
        mRecyclerView = root.findViewById(R.id.list) as RecyclerView
        val layoutManager = LinearLayoutManager(activity)
        mRecyclerView!!.layoutManager = layoutManager
        mAdapter = AlbumsAdapter(activity, mOnItemClickListener)
        mRecyclerView!!.adapter = mAdapter

        mPresenter = AlbumsPresenter(this)
        updateAlbums()
    }

    override fun onDestroyView() {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(mReceiver)
        super.onDestroyView()
    }

    private fun updateAlbums() {
        mLoading!!.visibility = View.VISIBLE
        mPresenter!!.getAlbums(mArtistName)
    }

    fun fetchAlbumsSucc(list: List<AlbumInfo>?) {
        mLoading!!.visibility = View.GONE
        if (list != null) {
            mAdapter!!.setAlbums(list)
        }
    }

    fun fetchAlbumsFail() {
        mLoading!!.visibility = View.GONE
    }

    companion object {
        val ARTIST_NAME = "artist_name"
    }
}
