package isel.yawa.present

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import isel.yawa.R
import kotlinx.android.synthetic.main.activity_credits.*

class CreditsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)

        api_icon.setOnClickListener { openPage(resources.getString(R.string.api_link)) }

        luis_github.text = makeBoxTextFor(R.string.luis, R.string.luis_github)
        pedro_github.text = makeBoxTextFor(R.string.pedro, R.string.pedro_github)
        joao_github.text = makeBoxTextFor(R.string.joao, R.string.joao_github)

        val onClick = { t: View ->
            val tv = (t as TextView)
            openPage("${resources.getString(R.string.github_href)}${tv.text.split(" - ")[1]}") // yolo
        }

        luis_github.setOnClickListener(onClick)
        pedro_github.setOnClickListener(onClick)
        joao_github.setOnClickListener(onClick)
    }

    private fun openPage(uri : String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))

    private fun makeBoxTextFor(name: Int, gitHubProfile: Int): String = "${resources.getString(name)} - ${resources.getString(gitHubProfile)}"
}
