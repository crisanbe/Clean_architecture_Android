package com.platzi.android.rickandmorty.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.platzi.android.rickandmorty.R
import com.platzi.android.rickandmorty.adapters.FavoriteListAdapter
import com.platzi.android.rickandmorty.api.APIConstants.BASE_API_URL
import com.platzi.android.rickandmorty.api.CharacterRequest
import com.platzi.android.rickandmorty.database.CharacterDao
import com.platzi.android.rickandmorty.database.CharacterDatabase
import com.platzi.android.rickandmorty.database.CharacterEntity
import com.platzi.android.rickandmorty.databinding.FragmentFavoriteListBinding
import com.platzi.android.rickandmorty.presentation.FavoriteListViewModel
import com.platzi.android.rickandmorty.utils.setItemDecorationSpacing
import kotlinx.android.synthetic.main.fragment_favorite_list.*

class FavoriteListFragment : Fragment() {

    //region Fields

    private lateinit var favoriteListAdapter: FavoriteListAdapter
    private lateinit var listener: OnFavoriteListFragmentListener
    private lateinit var characterRequest: CharacterRequest
    private lateinit var characterDao: CharacterDao

    private val favoriteListViewModel: FavoriteListViewModel by lazy {
        FavoriteListViewModel(characterDao)
    }

    //endregion

    //region Override Methods & Callbacks

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            listener = context as OnFavoriteListFragmentListener
        }catch (e: ClassCastException){
            throw ClassCastException("$context must implement OnFavoriteListFragmentListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        characterRequest = CharacterRequest(BASE_API_URL)
        characterDao = CharacterDatabase.getDatabase(activity!!.applicationContext).characterDao()

        return DataBindingUtil.inflate<FragmentFavoriteListBinding>(
            inflater,
            R.layout.fragment_favorite_list,
            container,
            false
        ).apply {
            lifecycleOwner = this@FavoriteListFragment
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteListAdapter = FavoriteListAdapter { character ->
            listener.openCharacterDetail(character)
        }
        favoriteListAdapter.setHasStableIds(true)

        rvFavoriteList.run {
            setItemDecorationSpacing(resources.getDimension(R.dimen.list_item_padding))
            adapter = favoriteListAdapter
        }

        //validar el contenido de la lista
        favoriteListViewModel.favoriteCharacterList.observe(viewLifecycleOwner, Observer(favoriteListViewModel::onFavoriteCharacterList ))
        //validar los eventos
        favoriteListViewModel.events.observe(viewLifecycleOwner, Observer{ events->
            events?.getContentIfNotHandled()?.let {  navigation ->
                when(navigation){                                                     //navigation.run para tenr acceso a su informacion
                   is FavoriteListViewModel.FavoriteListNavigation.ShowCharacterList-> navigation.run {
                       tvEmptyListMessage.isVisible = false //ocultamos la etiqueta
                       favoriteListAdapter.updateData(characterList)
                   }
                   FavoriteListViewModel.FavoriteListNavigation.ShowEmtyListMessage-> {
                       tvEmptyListMessage.isVisible = false //ocultamos la etiqueta
                       favoriteListAdapter.updateData(emptyList())
                   }
                }
            }
        })
    }
    //endregion

    //region Private Methods

    //endregion

    //region Inner Classes & Interfaces

    interface OnFavoriteListFragmentListener {
        fun openCharacterDetail(character: CharacterEntity)
    }

    //endregion

    //region Companion object

    companion object {

        fun newInstance(args: Bundle? = Bundle()) = FavoriteListFragment().apply {
            arguments = args
        }
    }

    //endregion

}
