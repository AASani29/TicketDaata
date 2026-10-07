import React, { useId } from 'react';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label: string;
  icon?: React.ReactNode;
  error?: string;
}

export const Input: React.FC<InputProps> = ({ label, icon, error, id, className = '', ...rest }) => {
  const generatedId = useId();
  const inputId = id ?? generatedId;

  return (
    <div>
      <label htmlFor={inputId} className="block text-sm font-medium text-secondary-700 mb-1.5">
        {label}
      </label>
      <div className="relative">
        {icon && (
          <span className="absolute inset-y-0 left-0 pl-3 flex items-center text-secondary-400 pointer-events-none">
            {icon}
          </span>
        )}
        <input
          id={inputId}
          className={`w-full py-2 border rounded-lg text-sm text-secondary-900 placeholder:text-secondary-400
            focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500
            ${icon ? 'pl-10 pr-3' : 'px-3'}
            ${error ? 'border-red-400' : 'border-secondary-300'}
            ${className}`}
          aria-invalid={!!error}
          {...rest}
        />
      </div>
      {error && <p className="mt-1.5 text-sm text-red-600">{error}</p>}
    </div>
  );
};

interface TextareaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label: string;
  error?: string;
}

export const Textarea: React.FC<TextareaProps> = ({ label, error, id, className = '', ...rest }) => {
  const generatedId = useId();
  const inputId = id ?? generatedId;

  return (
    <div>
      <label htmlFor={inputId} className="block text-sm font-medium text-secondary-700 mb-1.5">
        {label}
      </label>
      <textarea
        id={inputId}
        className={`w-full px-3 py-2 border rounded-lg text-sm text-secondary-900 placeholder:text-secondary-400
          focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500
          ${error ? 'border-red-400' : 'border-secondary-300'}
          ${className}`}
        aria-invalid={!!error}
        {...rest}
      />
      {error && <p className="mt-1.5 text-sm text-red-600">{error}</p>}
    </div>
  );
};

interface SelectOption {
  value: string;
  label: string;
}

interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label: string;
  options: SelectOption[];
  error?: string;
}

export const Select: React.FC<SelectProps> = ({ label, options, error, id, className = '', ...rest }) => {
  const generatedId = useId();
  const inputId = id ?? generatedId;

  return (
    <div>
      <label htmlFor={inputId} className="block text-sm font-medium text-secondary-700 mb-1.5">
        {label}
      </label>
      <select
        id={inputId}
        className={`w-full px-3 py-2 border rounded-lg text-sm text-secondary-900 bg-white
          focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500
          ${error ? 'border-red-400' : 'border-secondary-300'}
          ${className}`}
        aria-invalid={!!error}
        {...rest}
      >
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
      {error && <p className="mt-1.5 text-sm text-red-600">{error}</p>}
    </div>
  );
};

export default Input;
