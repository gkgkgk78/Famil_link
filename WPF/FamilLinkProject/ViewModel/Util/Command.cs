using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace FamilLinkProject.ViewModel.Util
{
    public class Command : ICommand
    {
        Action<object> _executeMethod;
    Func<object, bool> _canexecuteMethod;
    public event EventHandler CanExecuteChanged;
    public Command(Action<object> executeMethod, Func<object, bool> canexecuteMethod)
    {
        this._executeMethod = executeMethod;
        this._canexecuteMethod = canexecuteMethod;
    }


    public bool CanExecute(object parameter)
    {
        return true;
    }

    public void Execute(object parameter)
    {
        _executeMethod(parameter);
    }
}
}
