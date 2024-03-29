package sheridan.yamazaki.businessparagon.ui.business.checkout

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import sheridan.yamazaki.businessparagon.R
import sheridan.yamazaki.businessparagon.databinding.CheckoutFragmentBinding
import sheridan.yamazaki.businessparagon.model.Business
import sheridan.yamazaki.businessparagon.model.Product
import sheridan.yamazaki.businessparagon.ui.authentication.SignUpFragment
import sheridan.yamazaki.businessparagon.ui.business.list.BusinessListFragment
import sheridan.yamazaki.businessparagon.ui.business.payment.PaymentFragment


@AndroidEntryPoint
class CheckoutFragment: Fragment() {
    private lateinit var binding: CheckoutFragmentBinding
    private val viewModel: CheckoutViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private var businessId: String = ""
    private var currentUserId: String = ""
    private val displayList = ArrayList<Product>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth
        currentUserId = auth.currentUser.uid
        businessId = requireArguments().getString("id").toString()
        val userEmail = if (requireArguments().getString("email") != null) {
            requireArguments().getString("email").toString()
        }else auth.currentUser.email.toString()

        //get business layout and user shopping cart from the view model
        if (businessId.isNotEmpty()) {
            viewModel.returnLayout(businessId, "checkout")
            viewModel.loadData(businessId)
        }

        if (currentUserId.isNotEmpty()){
            viewModel.returnShoppingCart(currentUserId)
        }

        binding = CheckoutFragmentBinding.inflate(inflater, container, false)

        binding.email.setText(userEmail)
        binding.paymentButton.setOnClickListener { proceedToPayment() }

        val text = "Looking for more? Continue Shopping"
        val spannableString = SpannableString(text)
        val signUpFragment = SignUpFragment()

        val continueShoppingClickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                returnToExploreView()
            }
        }

        spannableString.setSpan(continueShoppingClickableSpan, 18, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.continueShopping.setText(spannableString, TextView.BufferType.SPANNABLE)
        binding.continueShopping.movementMethod = LinkMovementMethod.getInstance()

        //set checkout product list adapter and have remove functionality click
        val adapter = CheckoutListAdapter(displayList, false, onClick = {
            displayList[it].shoppingCartId?.let { it1 -> viewModel.removeFromUserShoppingCart(currentUserId, it1) }
            updateList(it)
        })

        binding.recyclerCheckout.adapter = adapter

        //set checkout product list on the view
        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
            adapter.products.addAll(products)
            var subtotal = 0.00
            for (item in products){
                subtotal += item.unitPrice?.times(item.quantity!!)!!
            }
            updatePrice(subtotal)
        }

        viewModel.business.observe(viewLifecycleOwner) { business ->
            val toolbar = (requireActivity() as AppCompatActivity)
            toolbar.title = business.name
        }

        //set checkout design layout and change layout dynamically
        viewModel.layout.observe(viewLifecycleOwner) { layout ->
            view?.setBackgroundColor(Color.parseColor(layout.backgroundColor))
            val normalFontStyle = layout.normalTextStyle?.let { getFontStyleEnum(it) }
            val titleFontStyle = layout.titleTextStyle?.let { getFontStyleEnum(it) }
            val subtitleFontStyle = layout.subtitleTextStyle?.let { getFontStyleEnum(it) }
            val alignment = layout.alignment?.let { getAlignmentEnum(it) }

            if (alignment != null) {
                binding.shoppingCartTitle.gravity = alignment
                binding.checkoutTitle.gravity = alignment
                binding.continueShopping.gravity = alignment
                binding.checkboxUpdates.gravity = alignment
                binding.emailText.gravity = alignment
                binding.nextStepTitle.gravity = alignment
                binding.paymentInfo.gravity = alignment
                binding.paymentInfoText.gravity = alignment
                binding.orderInfo.gravity = alignment
                binding.orderInfoText.gravity = alignment
                binding.email.gravity = alignment
            }
            binding.shoppingCartTitle.setTextColor(Color.parseColor(layout.titleTextColor))
            binding.subtotalTitle.setTextColor(Color.parseColor(layout.subtitleTextColor))
            binding.HSTTitle.setTextColor(Color.parseColor(layout.subtitleTextColor))
            binding.totalTitle.setTextColor(Color.parseColor(layout.subtitleTextColor))
            binding.subtotal.setTextColor(Color.parseColor(layout.subtitleTextColor))
            binding.HST.setTextColor(Color.parseColor(layout.subtitleTextColor))
            binding.total.setTextColor(Color.parseColor(layout.subtitleTextColor))
            binding.checkoutTitle.setTextColor(Color.parseColor(layout.titleTextColor))
            binding.continueShopping.setTextColor(Color.parseColor(layout.normalTextColor))
            binding.checkboxUpdates.setTextColor(Color.parseColor(layout.normalTextColor))
            binding.emailText.setTextColor(Color.parseColor(layout.normalTextColor))
            binding.email.setTextColor(Color.parseColor(layout.normalTextColor))
            binding.nextStepTitle.setTextColor(Color.parseColor(layout.titleTextColor))
            binding.paymentInfo.setTextColor(Color.parseColor(layout.subtitleTextColor))
            binding.paymentInfoText.setTextColor(Color.parseColor(layout.normalTextColor))
            binding.orderInfo.setTextColor(Color.parseColor(layout.subtitleTextColor))
            binding.orderInfoText.setTextColor(Color.parseColor(layout.normalTextColor))

            binding.shoppingCartTitle.typeface = titleFontStyle?.let { Typeface.create(layout.titleTextFont, it) };
            binding.subtotalTitle.typeface = subtitleFontStyle?.let { Typeface.create(layout.subtitleTextFont, it) };
            binding.HSTTitle.typeface = subtitleFontStyle?.let { Typeface.create(layout.subtitleTextFont, it) };
            binding.totalTitle.typeface = subtitleFontStyle?.let { Typeface.create(layout.subtitleTextFont, it) };
            binding.subtotal.typeface = subtitleFontStyle?.let { Typeface.create(layout.subtitleTextFont, it) };
            binding.HST.typeface = subtitleFontStyle?.let { Typeface.create(layout.subtitleTextFont, it) };
            binding.total.typeface = subtitleFontStyle?.let { Typeface.create(layout.subtitleTextFont, it) };
            binding.checkoutTitle.typeface = titleFontStyle?.let { Typeface.create(layout.titleTextFont, it) };
            binding.continueShopping.typeface = normalFontStyle?.let { Typeface.create(layout.normalTextFont, it) };
            binding.checkboxUpdates.typeface = normalFontStyle?.let { Typeface.create(layout.normalTextFont, it) };
            binding.emailText.typeface = normalFontStyle?.let { Typeface.create(layout.normalTextFont, it) };
            binding.email.typeface = normalFontStyle?.let { Typeface.create(layout.normalTextFont, it) };
            binding.nextStepTitle.typeface = titleFontStyle?.let { Typeface.create(layout.titleTextFont, it) };
            binding.paymentInfo.typeface = subtitleFontStyle?.let { Typeface.create(layout.subtitleTextFont, it) };
            binding.paymentInfoText.typeface = normalFontStyle?.let { Typeface.create(layout.normalTextFont, it) };
            binding.orderInfo.typeface = subtitleFontStyle?.let { Typeface.create(layout.subtitleTextFont, it) };
            binding.orderInfoText.typeface = normalFontStyle?.let { Typeface.create(layout.normalTextFont, it) };
            binding.paymentButton.setBackgroundColor(Color.parseColor(layout.foregroundColor))

        }
        binding.executePendingBindings()
        return binding.root
    }

    private fun getFontStyleEnum(textStyle: String): Int {
        var fontStyle = 0
        fontStyle = when (textStyle){
            "normal" -> Typeface.NORMAL
            "bold" -> Typeface.BOLD
            else -> Typeface.ITALIC
        }
        return fontStyle
    }

    private fun getAlignmentEnum(alignment: String): Int {
        var alignmentStyle = 0
        alignmentStyle = when (alignment){
            "right" -> Gravity.RIGHT
            "left" -> Gravity.LEFT
            else -> Gravity.CENTER
        }
        return alignmentStyle
    }

    private fun updateList(position: Int){
        val newSubtotal = binding.subtotal.text.toString().drop(2).toDouble() - (displayList[position].unitPrice?.times(displayList[position].quantity!!)!!)
        updatePrice(newSubtotal)
        displayList.removeAt(position)
        binding.recyclerCheckout.adapter?.notifyDataSetChanged()

    }

    private fun updatePrice(subtotal: Double){
        binding.subtotal.text = "C$" + String.format("%.2f", subtotal)
        val HST = subtotal * 0.13
        val total = subtotal + HST
        binding.HST.text = "C$" + String.format("%.2f", HST)
        binding.total.text = "C$" + String.format("%.2f", total)
    }

    //proceed to payment once email is filled
    private fun proceedToPayment() {
        if(binding.email.text.toString().isEmpty()){
            Toast.makeText(
                    activity,
                    "Please fill out email for order confirmation!",
                    Toast.LENGTH_SHORT
            ).show()
        }else if (displayList.size <= 0){
            Toast.makeText(
                    activity,
                    "Please add products to the cart first!",
                    Toast.LENGTH_SHORT
            ).show()
        }
        else{
            val fragment = PaymentFragment()
            val bundle = Bundle()
            bundle.putString("id", businessId)
            bundle.putString("email",binding.email.text.toString())
            fragment.arguments = bundle
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.fl_wrapper, fragment)
                commit()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = (requireActivity() as AppCompatActivity)
        toolbar.supportActionBar?.show()
        toolbar.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        toolbar.title = "Checkout" ;
    }

    override fun onDestroyView() {
        val toolbar = (requireActivity() as AppCompatActivity)
        toolbar.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(false)
        toolbar.title = "Business Paragon"
        super.onDestroyView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        returnToExploreView()
        return true
    }

    private fun returnToExploreView(){
        val fragment = BusinessListFragment()
        val bundle = Bundle()
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }
}