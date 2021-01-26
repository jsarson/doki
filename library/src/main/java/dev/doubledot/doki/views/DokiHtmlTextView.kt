package dev.doubledot.doki.views

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Layout
import android.text.method.LinkMovementMethod
import android.text.style.AlignmentSpan
import android.util.AttributeSet
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatTextView
import dev.doubledot.doki.extensions.dpToPx
import io.noties.markwon.*
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.core.spans.CodeBlockSpan
import io.noties.markwon.core.spans.LastLineSpacingSpan
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.tag.SimpleTagHandler
import io.noties.markwon.image.ImagesPlugin
import java.util.*
import kotlin.math.roundToInt


class DokiHtmlTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    private val markwon = Markwon.builder(context)
        .usePlugin(ImagesPlugin.create())
        .usePlugin(HtmlPlugin.create())
        .usePlugin(object : AbstractMarkwonPlugin() {
            override fun configure(registry: MarkwonPlugin.Registry) {
                registry.require(HtmlPlugin::class.java) { htmlPlugin ->
                    htmlPlugin.addHandler(CodeBlockSpanHandler())
                    htmlPlugin.addHandler(FigCaptionHandler())
                }
            }

            override fun configureTheme(builder: MarkwonTheme.Builder) {
                builder.headingBreakHeight(0)
                    .linkColor(linkHighlightColor)
                    .blockMargin(24f.dpToPx.roundToInt())
                    .blockQuoteWidth(4f.dpToPx.roundToInt())
                    .codeTextColor(Color.GRAY)
                    .codeTypeface(Typeface.MONOSPACE)
            }
        })
        .build()

    var htmlText: String? = null
        set(value) {
            value?.let {
                markwon.setMarkdown(this, value)
            }
            field = value
        }

    var linkHighlightColor: Int = 0
        set(value) {
            field = value
            htmlText = htmlText
        }

    init {
        movementMethod = LinkMovementMethod.getInstance()
    }

    internal class CodeBlockSpanHandler : SimpleTagHandler() {
        override fun getSpans(configuration: MarkwonConfiguration, renderProps: RenderProps, tag: HtmlTag): Any {
            return CodeBlockSpan(configuration.theme())
        }

        @NonNull
        override fun supportedTags(): Collection<String> {
            return Collections.singleton("code")
        }
    }

    internal class FigCaptionHandler : SimpleTagHandler() {
        override fun getSpans(configuration: MarkwonConfiguration, renderProps: RenderProps, tag: HtmlTag): Any {
            return AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)
        }

        @NonNull
        override fun supportedTags(): Collection<String> {
            return Collections.singleton("figcaption")
        }
    }


}