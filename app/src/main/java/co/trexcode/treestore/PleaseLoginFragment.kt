package co.trexcode.treestore


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_please_login.view.*


class PleaseLoginFragment : Fragment() {

    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rootView = inflater.inflate(R.layout.fragment_please_login, container, false)

        val intent = Intent(rootView.context, LoginActivity::class.java)
        rootView.btnLogin.setOnClickListener {
            rootView.context.startActivity(intent)
        }

        return rootView

    }


}
